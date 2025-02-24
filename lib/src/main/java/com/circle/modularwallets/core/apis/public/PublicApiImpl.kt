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

package com.circle.modularwallets.core.apis.public

import com.circle.modularwallets.core.apis.util.UtilApiImpl
import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.models.Block
import com.circle.modularwallets.core.models.EstimateFeesPerGasResult
import com.circle.modularwallets.core.transports.RpcRequest
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.encoding.bigIntegerToHex
import com.circle.modularwallets.core.utils.encoding.hexToBigInteger
import com.circle.modularwallets.core.utils.rpc.performJsonRpcRequest
import org.web3j.protocol.core.methods.request.Transaction
import java.math.BigInteger
import kotlin.math.ceil
import kotlin.math.pow

internal object PublicApiImpl : PublicApi {

    override suspend fun getChainId(transport: Transport): String {
        val req = RpcRequest("eth_chainId")
        val result: String = performJsonRpcRequest(transport, req) as String
        return result
    }

    override suspend fun call(
        transport: Transport,
        from: String?,
        to: String,
        data: String
    ): String {
        val transaction = Transaction.createEthCallTransaction(from, to, data)
        val req = RpcRequest("eth_call", listOf<Any>(transaction, "latest"))
        val result: String = performJsonRpcRequest(transport, req) as String
        return result
    }

    override suspend fun estimateFeesPerGas(
        transport: Transport,
        type: FeeValuesType
    ): EstimateFeesPerGasResult {
        val baseFeeMultiplier = 1.2
        val block = getBlock(transport)
        if (type == FeeValuesType.eip1559) {
            block.baseFeePerGas?.let {
                val maxPriorityFeePerGas = estimateMaxPriorityFeePerGas(transport, block)
                val baseFeePerGas = multiply(it, baseFeeMultiplier)
                val maxFeePerGas = baseFeePerGas + maxPriorityFeePerGas
                return EstimateFeesPerGasResult(maxFeePerGas, maxPriorityFeePerGas)
            }
            throw BaseError("Eip1559FeesNotSupportedError")
        }
        return EstimateFeesPerGasResult(gasPrice = getGasPrice(transport))
    }

    override suspend fun getBalance(
        transport: Transport,
        address: String,
        blockNumber: BigInteger
    ): BigInteger {
        val hex = bigIntegerToHex(blockNumber)
        hex?.let {
            return getBalance(transport, address, hex)
        }
        return getBalance(transport, address)
    }

    override suspend fun getBalance(
        transport: Transport,
        address: String,
        blockNumberHexOrTag: String
    ): BigInteger {
        val req = RpcRequest("eth_getBalance", listOf<Any>(address, blockNumberHexOrTag))
        val result: String = performJsonRpcRequest(transport, req) as String
        return hexToBigInteger(result) ?: throw BaseError("Failed to transform to BigInteger")
    }

    override suspend fun getBlockNum(transport: Transport): BigInteger {
        val req = RpcRequest("eth_blockNumber")
        val result: String = performJsonRpcRequest(transport, req) as String
        return hexToBigInteger(result) ?: throw BaseError("Failed to transform to BigInteger")
    }

    override suspend fun getBlock(
        transport: Transport,
        includeTransactions: Boolean,
        blockNumber: BigInteger
    ): Block {
        val hex = bigIntegerToHex(blockNumber)
        hex?.let {
            return getBlock(transport, includeTransactions, hex)
        }
        return getBlock(transport, includeTransactions)
    }

    override suspend fun getBlock(
        transport: Transport,
        includeTransactions: Boolean,
        blockNumberHexOrTag: String
    ): Block {
        val req = RpcRequest(
            "eth_getBlockByNumber",
            listOf<Any>(blockNumberHexOrTag, includeTransactions)
        )
        val result = performJsonRpcRequest(transport, req, BlockRpc::class.java)
        return result.first.toBlock()
    }

    override suspend fun getGasPrice(
        transport: Transport,
    ): BigInteger {
        val req = RpcRequest("eth_gasPrice")
        val result: String = performJsonRpcRequest(transport, req) as String
        return hexToBigInteger(result) ?: throw BaseError("Failed to transform to BigInteger")
    }

    override suspend fun getCode(
        transport: Transport,
        address: String,
        blockNumber: BigInteger
    ): String {
        val hex = bigIntegerToHex(blockNumber)
        hex?.let {
            return getCode(transport, address, hex)
        }
        return getCode(transport, address)
    }

    override suspend fun getCode(
        transport: Transport,
        address: String,
        blockNumberHexOrTag: String
    ): String {
        val req = RpcRequest("eth_getCode", listOf(address, blockNumberHexOrTag))
        val result: String = performJsonRpcRequest(transport, req) as String
        return result

    }

    private fun multiply(base: BigInteger, baseFeeMultiplier: Double): BigInteger {
        val decimals = baseFeeMultiplier.toString().split(".").getOrNull(1)?.length ?: 0
        val denominator = 10.0.pow(decimals).toLong()
        val scaledMultiplier = ceil(baseFeeMultiplier * denominator).toLong()
        return (base * BigInteger.valueOf(scaledMultiplier)) / BigInteger.valueOf(denominator)
    }

    private suspend fun estimateMaxPriorityFeePerGas(
        transport: Transport,
        block: Block
    ): BigInteger {
        return try {
            UtilApiImpl.getMaxPriorityFeePerGas(transport)
        } catch (e: Throwable) {
            estimateMaxPriorityFeePerGasFallback(transport, block)
        }
    }

    internal suspend fun estimateMaxPriorityFeePerGasFallback(
        transport: Transport,
        block: Block
    ): BigInteger {
        block.baseFeePerGas?.let {
            val gasPrice = getGasPrice(transport)
            val maxPriorityFeePerGas = gasPrice - it
            return maxPriorityFeePerGas.max(BigInteger.ZERO)
        }
        throw BaseError("Eip1559FeesNotSupportedError")
    }
}

