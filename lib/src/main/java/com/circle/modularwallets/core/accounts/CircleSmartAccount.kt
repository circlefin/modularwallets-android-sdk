/*
 * Copyright 2025 Circle Internet Group, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 * Http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.circle.modularwallets.core.accounts

import android.content.Context
import com.circle.modularwallets.core.BuildConfig
import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.apis.modular.ModularApiImpl
import com.circle.modularwallets.core.apis.modular.ModularWallet
import com.circle.modularwallets.core.apis.modular.ScaConfiguration
import com.circle.modularwallets.core.apis.modular.getCreateWalletReq
import com.circle.modularwallets.core.apis.public.PublicApiImpl
import com.circle.modularwallets.core.apis.util.UtilApiImpl
import com.circle.modularwallets.core.clients.Client
import com.circle.modularwallets.core.constants.CIRCLE_SMART_ACCOUNT_VERSION
import com.circle.modularwallets.core.constants.CIRCLE_SMART_ACCOUNT_VERSION_V1
import com.circle.modularwallets.core.constants.CIRCLE_WEIGHTED_WEB_AUTHN_MULTISIG_PLUGIN
import com.circle.modularwallets.core.constants.EIP712_PREFIX
import com.circle.modularwallets.core.constants.FACTORY
import com.circle.modularwallets.core.constants.PUBLIC_KEY_OWN_WEIGHT
import com.circle.modularwallets.core.constants.REPLAY_SAFE_HASH_V1
import com.circle.modularwallets.core.constants.SALT
import com.circle.modularwallets.core.constants.STUB_SIGNATURE
import com.circle.modularwallets.core.constants.THRESHOLD_WEIGHT
import com.circle.modularwallets.core.models.EncodeCallDataArg
import com.circle.modularwallets.core.models.EntryPoint
import com.circle.modularwallets.core.models.EstimateUserOperationGasResult
import com.circle.modularwallets.core.models.SignResult
import com.circle.modularwallets.core.models.UserOperation
import com.circle.modularwallets.core.models.UserOperationV07
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.FunctionParameters
import com.circle.modularwallets.core.utils.NonceManager
import com.circle.modularwallets.core.utils.NonceManagerSource
import com.circle.modularwallets.core.utils.abi.encodeAbiParameters
import com.circle.modularwallets.core.utils.abi.encodeCallData
import com.circle.modularwallets.core.utils.abi.encodePacked
import com.circle.modularwallets.core.utils.data.concat
import com.circle.modularwallets.core.utils.data.pad
import com.circle.modularwallets.core.utils.data.slice
import com.circle.modularwallets.core.utils.encoding.bytesToHex
import com.circle.modularwallets.core.utils.encoding.stringToHex
import com.circle.modularwallets.core.utils.encoding.toBytes
import com.circle.modularwallets.core.utils.encoding.toSha3Bytes
import com.circle.modularwallets.core.utils.signature.hashMessage
import com.circle.modularwallets.core.utils.signature.hashTypedData
import com.circle.modularwallets.core.utils.signature.parseP256Signature
import com.circle.modularwallets.core.utils.smartAccount.getMinimumVerificationGasLimit
import com.circle.modularwallets.core.utils.userOperation.getUserOperationHash
import com.circle.modularwallets.core.utils.userOperation.parseFactoryAddressAndData
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.DynamicArray
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.DynamicStruct
import org.web3j.abi.datatypes.StaticStruct
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.datatypes.generated.Uint8
import org.web3j.crypto.Hash
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal suspend fun getModularWalletAddress(
    transport: Transport, hexPublicKey: String, version: String, name: String? = null
): ModularWallet {
    val (x, y) = parseP256Signature(hexPublicKey)
    val wallet =
        ModularApiImpl.getAddress(
            transport,
            getCreateWalletReq(x.toString(), y.toString(), version, name)
        )
    return wallet
}

internal suspend fun getComputeWallet(
    client: Client,
    owner: Account<SignResult>,
    version: String
): ModularWallet {
    return ModularWallet(
        address = getAddressFromWebAuthnOwner(client.transport, owner.getAddress()),
        scaConfiguration = ScaConfiguration(
            scaCore = version,
        ),
    )
}

internal fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val currentDate = Date()
    return dateFormat.format(currentDate)
}

internal fun getDefaultWalletName(): String {
    return "passkey-${getCurrentDateTime()}"
}

/**
 * Creates a Circle smart account.
 *
 * @param client The client used to interact with the blockchain.
 * @param owner The owner account associated with the Circle smart account.
 * @param version The version of the Circle smart account. Default is "circle_passkey_account_v1".
 * @param name The wallet name assigned to the newly registered account defaults to the passkey username provided by the end user.
 * @return The created Circle smart account.
 */

@Throws(Exception::class)
@JvmOverloads
suspend fun toCircleSmartAccount(
    client: Client,
    owner: Account<SignResult>,
    version: String = CIRCLE_SMART_ACCOUNT_VERSION_V1,
    name: String = getDefaultWalletName()
): CircleSmartAccount {
    val actualVersion = CIRCLE_SMART_ACCOUNT_VERSION[version] ?: version
    val wallet =
        try {
            getModularWalletAddress(client.transport, owner.getAddress(), actualVersion, name)
        } catch (e: Throwable) {
            if (BuildConfig.INTERNAL_BUILD) {
                getComputeWallet(client, owner, actualVersion)
            } else {
                throw e
            }
        }
    val account = CircleSmartAccount(
        client, owner, wallet
    )
    return account
}

/**
 * Class representing a Circle smart account.
 *
 * @param client The client used to interact with the blockchain.
 * @param owner The owner account associated with the Circle Smart account.
 * @param wallet The response containing the created wallet information.
 * @param entryPoint The entry point for the smart account. Default is EntryPoint.V07.
 */

class CircleSmartAccount(
    client: Client,
    private val owner: Account<SignResult>,
    internal val wallet: ModularWallet,
    entryPoint: EntryPoint = EntryPoint.V07
) : SmartAccount(client, entryPoint) {
    private var deployed = false
    private val nonceManager = NonceManager(object : NonceManagerSource {
        override fun get(parameters: FunctionParameters): BigInteger {
            return BigInteger.valueOf(System.currentTimeMillis())
        }

        override fun set(parameters: FunctionParameters, nonce: BigInteger) {
        }
    })

    /**
     * Configuration for the user operation.
     */
    override var userOperation: UserOperationConfiguration? =
        UserOperationConfiguration { userOperation ->
            val minimumVerificationGasLimit =
                getMinimumVerificationGasLimit(isDeployed(), client.chain.chainId)
            EstimateUserOperationGasResult(
                verificationGasLimit = minimumVerificationGasLimit
                    .max(userOperation.verificationGasLimit ?: BigInteger.ZERO)
            )
        }

    /**
     * Returns the address of the Circle smart account.
     *
     * @return The address of the Circle smart account.
     */
    override fun getAddress(): String {
        return wallet.address
    }

    /**
     * Encodes the given call data arguments.
     *
     * @param args The call data arguments to encode.
     * @return The encoded call data.
     */
    override fun encodeCalls(args: Array<EncodeCallDataArg>): String {
        return encodeCallData(args)
    }

    /**
     * Returns the factory arguments if the account is not deployed.
     *
     * @return The factory arguments or null if already deployed.
     */
    override suspend fun getFactoryArgs(): Pair<String, String>? {
        if (isDeployed()) {
            return null
        }
        wallet.scaConfiguration.initCode?.let {
            return parseFactoryAddressAndData(it)
        }
        return Pair(FACTORY.address, getFactoryData(owner.getAddress()))
    }

    /**
     * Checks if the account is deployed.
     *
     * @return True if the account is deployed, false otherwise.
     */

    @Throws(Exception::class)
    suspend fun isDeployed(): Boolean {
        if (deployed) {
            return true
        }
        try {
            val byteCode = PublicApiImpl.getCode(client.transport, getAddress())
            deployed = Numeric.hexStringToByteArray(byteCode).isNotEmpty()
            return deployed
        } catch (e: Throwable) {
            return false
        }
    }

    /**
     * Returns the nonce for the Circle smart account.
     *
     * @param key Optional key to retrieve the nonce for.
     * @return The nonce of the Circle smart account.
     */

    @Throws(Exception::class)
    override suspend fun getNonce(key: BigInteger?): BigInteger {
        val notNullKey =
            key ?: nonceManager.consume(FunctionParameters(getAddress(), client.chain.chainId))
        val nonce =
            UtilApiImpl.getNonce(client.transport, getAddress(), entryPoint.address, notNullKey)
        return nonce
    }

    /**
     * Returns the stub signature for the given user operation.
     *
     * @param userOp The user operation to retrieve the stub signature for. Type T must be the subclass of UserOperation.
     * @return The stub signature.
     */
    override fun <T : UserOperation> getStubSignature(userOp: T): String {
        return STUB_SIGNATURE
    }

    /**
     * Signs the given hex data.
     *
     * @param context The context used to launch framework UI flows ; use an activity context to make sure the UI will be launched within the same task stack.
     * @param hex The hex data to sign.
     * @return The signed data.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    override suspend fun sign(context: Context, hex: String): String {
        val digest = toSha3Bytes(hex)
        val hash = getReplaySafeHash(
            client.chain.chainId, getAddress(), bytesToHex(digest)
        )
        val signResult = owner.sign(context, hash)
        val signature = encodePackedForSignature(
            signResult,
            owner.getAddress(),
            false,
        )
        return signature
    }

    /**
     * Signs the given message.
     *
     * @param context The context used to launch framework UI flows ; use an activity context to make sure the UI will be launched within the same task stack.
     * @param message The message to sign.
     * @return The signed message.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    override suspend fun signMessage(context: Context, message: String): String {
        val digest = toSha3Bytes(hashMessage(message.toByteArray()))
        val hash = getReplaySafeHash(
            client.chain.chainId, getAddress(), bytesToHex(digest)
        )
        val signResult = owner.sign(context, hash)
        val signature = encodePackedForSignature(
            signResult,
            owner.getAddress(),
            false,
        )
        return signature
    }

    /**
     * Signs the given typed data.
     *
     * @param context The context used to launch framework UI flows ; use an activity context to make sure the UI will be launched within the same task stack.
     * @param typedData The typed data to sign.
     * @return The signed typed data.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    override suspend fun signTypedData(context: Context, typedData: String): String {
        val digest = toSha3Bytes(hashTypedData(typedData))
        val hash = getReplaySafeHash(
            client.chain.chainId, getAddress(), bytesToHex(digest)
        )
        val signResult = owner.sign(context, hash)
        val signature = encodePackedForSignature(
            signResult,
            owner.getAddress(),
            false,
        )
        return signature
    }

    /**
     * Signs the given user operation.
     *
     * @param context The context used to launch framework UI flows ; use an activity context to make sure the UI will be launched within the same task stack.
     * @param chainId The chain ID for the user operation. Default is the chain ID of the client.
     * @param userOp The user operation to sign.
     * @return The signed user operation.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    override suspend fun signUserOperation(
        context: Context, chainId: Long, userOp: UserOperationV07
    ): String {
        userOp.sender = getAddress()
        val userOpHash = getUserOperationHash(chainId, userOp = userOp)
        val hash = hashMessage(userOpHash)
        val signResult = owner.sign(context, hash)
        val signature = encodePackedForSignature(
            signResult,
            owner.getAddress(),
            true,
        )
        return signature
    }

    /**
     * Returns the initialization code for the Circle smart account.
     *
     * @return The initialization code.
     */
    override suspend fun getInitCode(): String? {
        return wallet.getInitCode()
    }

}

internal fun getReplaySafeHash(
    chainId: Long,
    account: String,
    hash: String,
    verifyingContract: String = CIRCLE_WEIGHTED_WEB_AUTHN_MULTISIG_PLUGIN.address,
): String {
    val prefix = Numeric.hexStringToByteArray(EIP712_PREFIX)
    val domainSeparatorTypeHash =
        toSha3Bytes(REPLAY_SAFE_HASH_V1.domainSeparatorType)

    val domainSeparator = toSha3Bytes(
        encodeAbiParameters(
            listOf(
                Bytes32(domainSeparatorTypeHash),
                Bytes32(getModuleIdHash()),
                Uint256(chainId),
                Address(verifyingContract),
                Bytes32(pad(toBytes(account), isRight = true)),
            )
        )
    )

    val structHash = toSha3Bytes(
        encodeAbiParameters(
            listOf(
                Bytes32(getModuleTypeHash()),
                Bytes32(Numeric.hexStringToByteArray(hash))
            )
        )
    )
    return bytesToHex(
        Hash.sha3(
            concat(
                prefix,
                domainSeparator,
                structHash
            )
        )
    )
}

internal fun getModuleIdHash(): ByteArray {
    return toSha3Bytes(
        encodePacked(
            listOf<Type<*>>(
                Utf8String(REPLAY_SAFE_HASH_V1.name),
                Utf8String(REPLAY_SAFE_HASH_V1.version),
            )
        )
    )
}

internal fun getModuleTypeHash(): ByteArray {
    return toSha3Bytes(
        REPLAY_SAFE_HASH_V1.moduleType
    )
}

internal fun encodePackedForSignature(
    signResult: SignResult,
    publicKey: String,
    hasUserOpGas: Boolean,
): String {
    val (x, y) = parseP256Signature(publicKey)
    val sender = getSender(x, y)

    val sigBytes = encodeWebAuthnSigDynamicPart(signResult)
    val formattedSender = getFormattedSender(sender)
    val sigType: Long = if (hasUserOpGas) 34 else 2
    val encoded =
        encodePacked(
            listOf<Type<*>>(
                Bytes32(formattedSender),
                Uint256(65), // dynamicPos
                Uint8(sigType),
                Uint256(sigBytes.size.toLong()),
                DynamicBytes(sigBytes),
            )
        )

    return encoded
}

internal fun encodeWebAuthnSigDynamicPart(signResult: SignResult): ByteArray {
    val (r, s) = parseP256Signature(signResult.signature)
    val encoded = encodeParametersWebAuthnSigDynamicPart(
        signResult.webAuthn.authenticatorData,
        signResult.webAuthn.clientDataJSON,
        signResult.webAuthn.challengeIndex.toLong(),
        signResult.webAuthn.typeIndex.toLong(),
        true,
        r,
        s
    )
    return Numeric.hexStringToByteArray(encoded)
}

internal fun encodeParametersWebAuthnSigDynamicPart(
    authenticatorData: String,
    clientDataJSON: String,
    challengeIndex: Long,
    typeIndex: Long,
    requireUserVerification: Boolean,
    r: BigInteger,
    s: BigInteger
): String {
    val encoded = encodeAbiParameters(
        listOf<Type<*>>(
            DynamicStruct(
                DynamicStruct(
                    DynamicBytes(Numeric.hexStringToByteArray(authenticatorData)),
                    DynamicBytes(Numeric.hexStringToByteArray(stringToHex(clientDataJSON))),
                    Uint256(challengeIndex),
                    Uint256(typeIndex),
                    Bool(requireUserVerification),
                ),
                Uint256(r),
                Uint256(s),
            )
        )
    )
    return encoded
}

internal fun getFormattedSender(sender: String): ByteArray {
    return Numeric.hexStringToByteArray(pad(slice(sender, 2)))
}

internal fun getPluginInstallParams(x: BigInteger, y: BigInteger): String {
    val encoded = encodeAbiParameters(
        listOf(
            DynamicArray(Address::class.java), DynamicArray(Uint256::class.java), DynamicArray(
                StaticStruct::class.java,
                StaticStruct(
                    Uint256(x),
                    Uint256(y),
                ),
            ), DynamicArray(
                Uint256::class.java, Uint256(PUBLIC_KEY_OWN_WEIGHT)
            ), Uint256(THRESHOLD_WEIGHT)
        )
    )
    return encoded
}

internal fun getInitializeUpgradableMSCAParams(x: BigInteger, y: BigInteger): String {
    val pluginInstallParams = getPluginInstallParams(x, y)
    val encoded = encodeAbiParameters(
        listOf(
            DynamicArray(
                Address::class.java,
                Address(CIRCLE_WEIGHTED_WEB_AUTHN_MULTISIG_PLUGIN.address),
            ),
            DynamicArray(
                Bytes32::class.java,
                Bytes32(CIRCLE_WEIGHTED_WEB_AUTHN_MULTISIG_PLUGIN.manifestHash),
            ),
            DynamicArray(
                DynamicBytes::class.java,
                DynamicBytes(Numeric.hexStringToByteArray(pluginInstallParams)),
            ),
        )
    )
    return encoded
}

internal fun getSender(x: BigInteger, y: BigInteger): String {
    val encoded = getSenderParams(x, y)
    return Hash.sha3(encoded)
}

internal fun getSenderParams(x: BigInteger, y: BigInteger): String {
    return encodeAbiParameters(
        listOf(
            Uint256(x),
            Uint256(y),
        )
    )
}

internal fun getFactoryData(publicKey: String): String {
    val (x, y) = parseP256Signature(publicKey)
    val sender = getSender(x, y)
    val initializeUpgradableMSCAParams = getInitializeUpgradableMSCAParams(x, y)
    val function = org.web3j.abi.datatypes.Function(
        "createAccount", listOf(
            Bytes32(Numeric.hexStringToByteArray(sender)),
            Bytes32(SALT),
            DynamicBytes(Numeric.hexStringToByteArray(initializeUpgradableMSCAParams)),
        ), listOf<TypeReference<*>>(object : TypeReference<Address>() {})
    )
    val factoryData = FunctionEncoder.encode(function)
    return factoryData
}

internal suspend fun getAddressFromWebAuthnOwner(transport: Transport, publicKey: String): String {
    val (x, y) = parseP256Signature(publicKey)
    val sender = getSender(x, y)
    val initializeUpgradableMSCAParams = getInitializeUpgradableMSCAParams(x, y)

    /** address, mixedSalt */
    val result = UtilApiImpl.getAddress(
        transport,
        FACTORY.address,
        Numeric.hexStringToByteArray(sender),
        SALT,
        Numeric.hexStringToByteArray(initializeUpgradableMSCAParams)
    )
    return result.first
}