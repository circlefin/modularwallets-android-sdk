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
import com.circle.modularwallets.core.accounts.implementations.CircleSmartAccountDelegate
import com.circle.modularwallets.core.accounts.implementations.LocalCircleSmartAccountDelegate
import com.circle.modularwallets.core.accounts.implementations.WebAuthnCircleSmartAccountDelegate
import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.apis.modular.ModularWallet
import com.circle.modularwallets.core.apis.public.PublicApiImpl
import com.circle.modularwallets.core.apis.util.UtilApiImpl
import com.circle.modularwallets.core.clients.Client
import com.circle.modularwallets.core.constants.CIRCLE_SMART_ACCOUNT_VERSION
import com.circle.modularwallets.core.constants.CIRCLE_SMART_ACCOUNT_VERSION_V1
import com.circle.modularwallets.core.constants.FACTORY
import com.circle.modularwallets.core.constants.STUB_SIGNATURE
import com.circle.modularwallets.core.models.EncodeCallDataArg
import com.circle.modularwallets.core.models.EntryPoint
import com.circle.modularwallets.core.models.EstimateUserOperationGasResult
import com.circle.modularwallets.core.models.UserOperation
import com.circle.modularwallets.core.models.UserOperationV07
import com.circle.modularwallets.core.utils.FunctionParameters
import com.circle.modularwallets.core.utils.NonceManager
import com.circle.modularwallets.core.utils.NonceManagerSource
import com.circle.modularwallets.core.utils.abi.encodeCallData
import com.circle.modularwallets.core.utils.signature.hashMessage
import com.circle.modularwallets.core.utils.signature.hashTypedData
import com.circle.modularwallets.core.utils.smartAccount.getDefaultVerificationGasLimit
import com.circle.modularwallets.core.utils.userOperation.getUserOperationHash
import com.circle.modularwallets.core.utils.userOperation.parseFactoryAddressAndData
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val currentDate = Date()
    return dateFormat.format(currentDate)
}

internal fun getDefaultWalletName(prefix: String): String {
    return "$prefix-${getCurrentDateTime()}"
}

/**
 * Creates a Circle smart account.
 *
 * @param client The client used to interact with the blockchain.
 * @param owner The owner account associated with the Circle smart account.
 * @param version The version of the Circle smart account. Default is "circle_passkey_account_v1".
 * @param name The wallet name assigned to the newly registered account defaults to defaults to passkey-{datetime}.
 * @return The created Circle smart account.
 */

@Throws(Exception::class)
@JvmOverloads
suspend fun toCircleSmartAccount(
    client: Client,
    owner: WebAuthnAccount,
    version: String = CIRCLE_SMART_ACCOUNT_VERSION_V1,
    name: String = getDefaultWalletName(WebAuthnCircleSmartAccountDelegate.WALLET_PREFIX)
): CircleSmartAccount {
    val delegate = WebAuthnCircleSmartAccountDelegate(owner)
    val actualVersion = CIRCLE_SMART_ACCOUNT_VERSION[version] ?: version
    val wallet =
        try {
            delegate.getModularWalletAddress(client.transport, actualVersion, name)
        } catch (e: Throwable) {
            if (BuildConfig.INTERNAL_BUILD) {
                delegate.getComputeWallet(client, actualVersion)
            } else {
                throw e
            }
        }
    val account = CircleSmartAccount(
        client, delegate, wallet
    )
    return account
}

/**
 * Creates a Circle smart account.
 *
 * @param client The client used to interact with the blockchain.
 * @param owner The owner account associated with the Circle smart account.
 * @param version The version of the Circle smart account. Default is "circle_passkey_account_v1".
 * @param name The wallet name assigned to the newly registered account defaults to wallet-{datetime}.
 * @return The created Circle smart account.
 */

@Throws(Exception::class)
@JvmOverloads
suspend fun toCircleSmartAccount(
    client: Client,
    owner: LocalAccount,
    version: String = CIRCLE_SMART_ACCOUNT_VERSION_V1,
    name: String = getDefaultWalletName(LocalCircleSmartAccountDelegate.WALLET_PREFIX)
): CircleSmartAccount {
    val delegate = LocalCircleSmartAccountDelegate(owner)
    val actualVersion = CIRCLE_SMART_ACCOUNT_VERSION[version] ?: version
    val wallet =
        try {
            delegate.getModularWalletAddress(client.transport, actualVersion, name)
        } catch (e: Throwable) {
            if (BuildConfig.INTERNAL_BUILD) {
                delegate.getComputeWallet(client, actualVersion)
            } else {
                throw e
            }
        }
    val account = CircleSmartAccount(
        client, delegate, wallet
    )
    return account
}

/**
 * Class representing a Circle smart account.
 *
 * @param client The client used to interact with the blockchain.
 * @param wallet The response containing the created wallet information.
 * @param entryPoint The entry point for the smart account. Default is EntryPoint.V07.
 */

class CircleSmartAccount(
    client: Client,
    private val delegate: CircleSmartAccountDelegate,
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
            val minimumVerificationGasLimit = getDefaultVerificationGasLimit(isDeployed(), client.transport)
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
        return Pair(FACTORY.address, delegate.getFactoryData())
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
     * messageHash The hash to sign.
     *
     * @param context The context used to launch framework UI flows ; use an activity context to make sure the UI will be launched within the same task stack.
     * @param messageHash The message to sign.
     * @return The signed data.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    override suspend fun sign(context: Context, messageHash: String): String {
        val replaySafeMessageHash =
            UtilApiImpl.getReplaySafeMessageHash(client.transport, getAddress(), messageHash)
        return delegate.signAndWrap(context, replaySafeMessageHash, false)
    }

    /**
     * Signs a [EIP-191 Personal Sign message](https://eips.ethereum.org/EIPS/eip-191).
     *
     * @param context The context used to launch framework UI flows ; use an activity context to make sure the UI will be launched within the same task stack.
     * @param message The message to sign.
     * @return The signed message.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    override suspend fun signMessage(context: Context, message: String): String {
        val hashedMessage = hashMessage(message.toByteArray())
        val replaySafeMessageHash =
            UtilApiImpl.getReplaySafeMessageHash(client.transport, getAddress(), hashedMessage)
        return delegate.signAndWrap(context, replaySafeMessageHash, false)
    }

    /**
     * Signs a given typed data.
     *
     * @param context The context used to launch framework UI flows ; use an activity context to make sure the UI will be launched within the same task stack.
     * @param typedData The typed data to sign.
     * @return The signed typed data.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    override suspend fun signTypedData(context: Context, typedData: String): String {
        val hashedTypedData = hashTypedData(typedData)
        val replaySafeMessageHash =
            UtilApiImpl.getReplaySafeMessageHash(client.transport, getAddress(), hashedTypedData)
        return delegate.signAndWrap(context, replaySafeMessageHash, false)
    }

    /**
     * Signs a given user operation.
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
        return delegate.signAndWrap(context, userOpHash, true)
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
