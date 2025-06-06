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

package com.circle.modularwallets.core.apis.util

import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.apis.public.PublicApiImpl.call
import com.circle.modularwallets.core.constants.CIRCLE_WEIGHTED_WEB_AUTHN_MULTISIG_PLUGIN
import com.circle.modularwallets.core.constants.EIP1271_VALID_SIGNATURE
import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.transports.RpcRequest
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.Logger
import com.circle.modularwallets.core.utils.encoding.bytesToHex
import com.circle.modularwallets.core.utils.encoding.hexToBigInteger
import com.circle.modularwallets.core.utils.rpc.performJsonRpcRequest
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.StaticStruct
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Bytes4
import org.web3j.abi.datatypes.generated.Uint192
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.utils.Numeric
import java.math.BigInteger

internal object UtilApiImpl : UtilApi {
    override suspend fun getAddress(
        transport: Transport,
        to: String,
        sender: ByteArray,
        salt: ByteArray,
        initializingData: ByteArray,
    ): Pair<String, String> {
        val function = Function(
            "getAddress",
            listOf<Type<*>>(Bytes32(sender), Bytes32(salt), DynamicBytes(initializingData)),
            listOf<TypeReference<*>>(
                object : TypeReference<Address>() {},
                object : TypeReference<Bytes32>() {})
        )
        val data = FunctionEncoder.encode(function)
        Logger.d(msg = "getAddress > call")
        val resp = call(transport, null, to, data)
        val decoded = FunctionReturnDecoder.decode(resp, function.outputParameters)
        return Pair(decoded[0].value as String, Numeric.toHexString(decoded[1].value as ByteArray))
    }

    override suspend fun createAccount(
        transport: Transport,
        to: String,
        sender: String,
        salt: ByteArray,
        initializingData: ByteArray,
    ): String {
        val function = Function(
            "createAccount",
            listOf<Type<*>>(
                Bytes32(Numeric.hexStringToByteArray(sender)),
                Bytes32(salt),
                DynamicBytes(initializingData)
            ),
            listOf<TypeReference<*>>(
                object : TypeReference<Address>() {})
        )
        val data = FunctionEncoder.encode(function)
        Logger.d(msg = "createAccount > call")
        val resp = call(transport, null, to, data)
        val decoded = FunctionReturnDecoder.decode(resp, function.outputParameters)
        return decoded[0].value as String
    }

    override suspend fun getNonce(
        transport: Transport,
        address: String,
        to: String,
        key: BigInteger
    ): BigInteger {
        val function = Function(
            "getNonce",
            listOf<Type<*>>(Address(address), Uint192(key)),
            listOf<TypeReference<*>>(object : TypeReference<Uint256>() {})
        )
        val data = FunctionEncoder.encode(function)
        Logger.d(msg = "getNonce > call")
        val resp = call(transport, null, to, data)
        return hexToBigInteger(resp) ?: throw BaseError("Failed to transform to BigInteger")
    }

    override suspend fun getMaxPriorityFeePerGas(
        transport: Transport,
    ): BigInteger {
        val req = RpcRequest("eth_maxPriorityFeePerGas")
        val result: String = performJsonRpcRequest(transport, req) as String
        return hexToBigInteger(result) ?: throw BaseError("Failed to transform to BigInteger")
    }

    @ExcludeFromGeneratedCCReport
    override suspend fun isValidSignature(
        transport: Transport,
        digest: String,
        signature: String,
        from: String,
        to: String
    ): Boolean {
        val function = Function(
            "isValidSignature",
            listOf<Type<*>>(
                Bytes32(Numeric.hexStringToByteArray(digest)),
                DynamicBytes(Numeric.hexStringToByteArray(signature))
            ),
            listOf<TypeReference<*>>(
                object : TypeReference<Bytes4>() {})
        )
        val data = FunctionEncoder.encode(function)
        Logger.d(
            msg = """
            isValidSignature > call
            Digest: $digest
            Signature: $signature
            From: $from
            To: $to
        """.trimIndent()
        )
        val resp = call(transport, from, to, data)
        val decoded = FunctionReturnDecoder.decode(resp, function.outputParameters)
        if (decoded.isEmpty()) {
            Logger.w(msg = "Empty response from isValidSignature call")
            return false
        }
        return EIP1271_VALID_SIGNATURE.contentEquals(decoded[0].value as ByteArray)
    }

    @ExcludeFromGeneratedCCReport
    override suspend fun isOwnerOf(
        transport: Transport,
        account: String,
        ownerToCheck: String,
    ): Boolean {
        val function = Function(
            "isOwnerOf",
            listOf<Type<*>>(
                Address(account),
                Address(ownerToCheck)
            ),
            listOf<TypeReference<*>>(
                object : TypeReference<Bool>() {})
        )
        val data = FunctionEncoder.encode(function)
        Logger.d(
            msg = """
            isOwnerOf > call
            Account: $account
            OwnerToCheck: $ownerToCheck
        """.trimIndent()
        )
        val resp = call(
            transport,
            from = account,
            to = CIRCLE_WEIGHTED_WEB_AUTHN_MULTISIG_PLUGIN.address,
            data
        )
        val decoded = FunctionReturnDecoder.decode(resp, function.outputParameters)
        if (decoded.isEmpty()) {
            Logger.w(msg = "Empty response from isOwner call")
            return false
        }
        return decoded[0].value as Boolean
    }
    @ExcludeFromGeneratedCCReport
    override suspend fun isOwnerOf(
        transport: Transport,
        account: String,
        xOfOwnerToCheck: String,
        yOfOwnerToCheck: String,
    ): Boolean {
        val publicKeys = StaticStruct(
            Uint256(BigInteger(xOfOwnerToCheck)),
            Uint256(BigInteger(yOfOwnerToCheck))
        )
        val function = Function(
            "isOwnerOf",
            listOf<Type<*>>(
                Address(account),
                publicKeys
            ),
            listOf<TypeReference<*>>(
                object : TypeReference<Bool>() {})
        )
        val data = FunctionEncoder.encode(function)
        Logger.d(
            msg = """
            isOwnerOf > call
            Account: $account
            xOfOwnerToCheck: $xOfOwnerToCheck
            yOfOwnerToCheck: $yOfOwnerToCheck
        """.trimIndent()
        )
        val resp = call(
            transport,
            from = account,
            to = CIRCLE_WEIGHTED_WEB_AUTHN_MULTISIG_PLUGIN.address,
            data
        )
        val decoded = FunctionReturnDecoder.decode(resp, function.outputParameters)
        if (decoded.isEmpty()) {
            Logger.w(msg = "Empty response from isOwner call")
            return false
        }
        return decoded[0].value as Boolean
    }

    override suspend fun getReplaySafeMessageHash(
        transport: Transport,
        account: String,
        hash: String
    ): String {
        val byte32Hash: Bytes32
        try {
            byte32Hash = Bytes32(Numeric.hexStringToByteArray(hash))
        } catch (e: UnsupportedOperationException) {
            throw BaseError("Invalid hash: $hash")
        }
        val function = Function(
            "getReplaySafeMessageHash",
            listOf<Type<*>>(Address(account), byte32Hash),
            listOf<TypeReference<*>>(
                object : TypeReference<Bytes32>() {})
        )
        val data = FunctionEncoder.encode(function)
        val resp = call(transport, account, account, data)
        val decoded = FunctionReturnDecoder.decode(resp, function.outputParameters)
        if (decoded.isEmpty()) {
            throw BaseError("Invalid account or empty response for: $account. Response: $resp")
        }
        val result = bytesToHex(decoded[0].value as ByteArray)
        Logger.d(
            msg = """
            getReplaySafeMessageHash > call
            Account: $account
            Hash: $hash
            Result: $result
        """.trimIndent()
        )
        return result
    }
}

