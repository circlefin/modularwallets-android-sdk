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
import com.circle.modularwallets.core.accounts.LocalAccount
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
import com.circle.modularwallets.core.constants.SIG_TYPE_FLAG_DIGEST
import com.circle.modularwallets.core.constants.THRESHOLD_WEIGHT
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.abi.encodeAbiParameters
import com.circle.modularwallets.core.utils.abi.encodePacked
import com.circle.modularwallets.core.utils.data.pad
import com.circle.modularwallets.core.utils.signature.deserializeSignature
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.DynamicArray
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.StaticStruct
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.datatypes.generated.Uint8
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric

internal class LocalCircleSmartAccountDelegate(val owner: LocalAccount) :
    CircleSmartAccountDelegate {

    override suspend fun getModularWalletAddress(
        transport: Transport,
        version: String,
        name: String?
    ): ModularWallet {
        return getModularWalletAddress(transport, owner.getAddress(), version, name)
    }

    override suspend fun getComputeWallet(client: Client, version: String): ModularWallet {
        return ModularWallet(
            address = getAddressFromLocalOwner(client.transport, owner.getAddress()),
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
        val signature =
            if (hasUserOpGas) owner.signMessage(context, hash) else owner.sign(context, hash)
        val signatureData = deserializeSignature(signature)
        return encodePackedForSignature(signatureData, hasUserOpGas)
    }

    companion object {
        const val WALLET_PREFIX = "wallet"
        internal suspend fun getModularWalletAddress(
            transport: Transport, address: String, version: String, name: String? = null
        ): ModularWallet {
            val wallet =
                ModularApiImpl.getAddress(
                    transport,
                    getCreateWalletReq(address, version, name)
                )
            return wallet
        }

        internal suspend fun getAddressFromLocalOwner(
            transport: Transport,
            address: String
        ): String {
            val sender = pad(address)
            val initializeUpgradableMSCAParams = getInitializeUpgradableMSCAParams(address)

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

        private fun getInitializeUpgradableMSCAParams(address: String): String {
            val pluginInstallParams = getPluginInstallParams(address)
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

        private fun getPluginInstallParams(address: String): String {
            val encoded = encodeAbiParameters(
                listOf(
                    DynamicArray(Address::class.java, Address(address)),
                    DynamicArray(
                        Uint256::class.java, Uint256(OWNER_WEIGHT)
                    ),
                    DynamicArray(StaticStruct::class.java),
                    DynamicArray(Uint256::class.java),
                    Uint256(THRESHOLD_WEIGHT)
                )
            )
            return encoded
        }

        private fun getFactoryData(address: String): String {
            val sender = pad(address)
            val initializeUpgradableMSCAParams = getInitializeUpgradableMSCAParams(address)
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

        /**
         * Wraps a raw Secp256k1 signature into the ABI-encoded format expected by smart contract.
         * */
        internal fun encodePackedForSignature(
            signatureData: Sign.SignatureData,
            hasUserOpGas: Boolean
        ): String {
            val sigType: Long =
                if (hasUserOpGas) signatureData.v[0].toLong() + SIG_TYPE_FLAG_DIGEST else signatureData.v[0].toLong()
            val encoded =
                encodePacked(
                    listOf<Type<*>>(
                        Bytes32(signatureData.r),
                        Bytes32(signatureData.s),
                        Uint8(sigType),
                    )
                )

            return encoded
        }
    }
}