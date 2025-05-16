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

package com.circle.modularwallets.core.accounts.implementations

import android.content.Context
import com.circle.modularwallets.core.accounts.WebAuthnAccount
import com.circle.modularwallets.core.apis.modular.ModularApiImpl
import com.circle.modularwallets.core.apis.modular.ModularWallet
import com.circle.modularwallets.core.apis.modular.ScaConfiguration
import com.circle.modularwallets.core.apis.modular.getCreateWalletReq
import com.circle.modularwallets.core.apis.util.UtilApiImpl
import com.circle.modularwallets.core.clients.Client
import com.circle.modularwallets.core.constants.CIRCLE_WEIGHTED_WEB_AUTHN_MULTISIG_PLUGIN
import com.circle.modularwallets.core.constants.FACTORY
import com.circle.modularwallets.core.constants.OWNER_WEIGHT
import com.circle.modularwallets.core.constants.SALT
import com.circle.modularwallets.core.constants.SIG_TYPE_SECP256R1
import com.circle.modularwallets.core.constants.SIG_TYPE_SECP256R1_DIGEST
import com.circle.modularwallets.core.constants.THRESHOLD_WEIGHT
import com.circle.modularwallets.core.models.SignResult
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.abi.encodeAbiParameters
import com.circle.modularwallets.core.utils.abi.encodePacked
import com.circle.modularwallets.core.utils.data.pad
import com.circle.modularwallets.core.utils.data.slice
import com.circle.modularwallets.core.utils.encoding.stringToHex
import com.circle.modularwallets.core.utils.signature.hashMessage
import com.circle.modularwallets.core.utils.signature.parseP256Signature
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.DynamicArray
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.DynamicStruct
import org.web3j.abi.datatypes.StaticStruct
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.datatypes.generated.Uint8
import org.web3j.crypto.Hash
import org.web3j.utils.Numeric
import java.math.BigInteger

internal class WebAuthnCircleSmartAccountDelegate(val owner: WebAuthnAccount) :
    CircleSmartAccountDelegate {

    override suspend fun getModularWalletAddress(
        transport: Transport, version: String, name: String?
    ): ModularWallet {
        return getModularWalletAddress(transport, owner.getAddress(), version, name)
    }


    override suspend fun getComputeWallet(
        client: Client,
        version: String
    ): ModularWallet {
        return ModularWallet(
            address = getAddressFromWebAuthnOwner(client.transport, owner.getAddress()),
            scaConfiguration = ScaConfiguration(
                scaCore = version,
            ),
        )
    }

    override fun getFactoryData(): String {
        return getFactoryData(owner.getAddress())
    }

    override suspend fun signAndWrap(
        context: Context,
        hash: String,
        hasUserOpGas: Boolean
    ): String {
        val targetHash = if (hasUserOpGas) hashMessage(hash) else hash
        val signResult = owner.sign(context, targetHash)
        val signature = encodePackedForSignature(
            signResult,
            owner.getAddress(),
            hasUserOpGas,
        )
        return signature
    }

    companion object {
        const val WALLET_PREFIX = "passkey"
        const val DYNAMIC_POSITION = 65L
        fun getInitializeUpgradableMSCAParams(x: BigInteger, y: BigInteger): String {
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

        internal fun encodePackedForSignature(
            signResult: SignResult,
            publicKey: String,
            hasUserOpGas: Boolean,
        ): String {
            val (x, y) = parseP256Signature(publicKey)
            val sender = getSender(x, y)

            val sigBytes = encodeWebAuthnSigDynamicPart(signResult)
            val formattedSender = getFormattedSender(sender)
            val sigType: Long = if (hasUserOpGas) SIG_TYPE_SECP256R1_DIGEST else SIG_TYPE_SECP256R1
            val encoded =
                encodePacked(
                    listOf<Type<*>>(
                        Bytes32(formattedSender),
                        Uint256(DYNAMIC_POSITION),
                        Uint8(sigType),
                        Uint256(sigBytes.size.toLong()),
                        DynamicBytes(sigBytes),
                    )
                )

            return encoded
        }

        private fun encodeWebAuthnSigDynamicPart(signResult: SignResult): ByteArray {
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

        private fun getPluginInstallParams(x: BigInteger, y: BigInteger): String {
            val encoded = encodeAbiParameters(
                listOf(
                    DynamicArray(Address::class.java),
                    DynamicArray(Uint256::class.java),
                    DynamicArray(
                        StaticStruct::class.java,
                        StaticStruct(
                            Uint256(x),
                            Uint256(y),
                        ),
                    ),
                    DynamicArray(
                        Uint256::class.java, Uint256(OWNER_WEIGHT)
                    ),
                    Uint256(THRESHOLD_WEIGHT)
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

        internal suspend fun getAddressFromWebAuthnOwner(
            transport: Transport,
            publicKey: String
        ): String {
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

        fun getFactoryData(publicKey: String): String {
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
    }
}