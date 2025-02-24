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

package com.circle.modularwallets.core.apis.bundler

import com.circle.modularwallets.core.models.EstimateUserOperationGasResult
import com.circle.modularwallets.core.models.UserOperationReceipt
import com.circle.modularwallets.core.models.GetUserOperationResult
import com.circle.modularwallets.core.models.Log
import com.circle.modularwallets.core.models.TransactionReceipt
import com.circle.modularwallets.core.models.UserOperationRpc
import com.circle.modularwallets.core.utils.encoding.hexToBigInteger
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigInteger

@JsonClass(generateAdapter = true)
internal data class EstimateUserOperationGasResp(
    @Json(name = "preVerificationGas") var preVerificationGasHex: String? = null,
    @Json(name = "verificationGasLimit") var verificationGasLimitHex: String? = null,
    @Json(name = "callGasLimit") var callGasLimitHex: String? = null,
    @Json(name = "paymasterVerificationGasLimit") var paymasterVerificationGasLimitHex: String? = null,
    @Json(name = "paymasterPostOpGasLimit") var paymasterPostOpGasLimitHex: String? = null,
) {
    val preVerificationGas: BigInteger?
        get() = hexToBigInteger(preVerificationGasHex)
    val verificationGasLimit: BigInteger?
        get() = hexToBigInteger(verificationGasLimitHex)
    val callGasLimit: BigInteger?
        get() = hexToBigInteger(callGasLimitHex)
    val paymasterVerificationGasLimit: BigInteger?
        get() = hexToBigInteger(paymasterVerificationGasLimitHex)
    val paymasterPostOpGasLimit: BigInteger?
        get() = hexToBigInteger(paymasterPostOpGasLimitHex)
}

internal fun EstimateUserOperationGasResp.toResult(): EstimateUserOperationGasResult {
    return EstimateUserOperationGasResult(
        preVerificationGas = this.preVerificationGas,
        verificationGasLimit = this.verificationGasLimit,
        callGasLimit = this.callGasLimit,
        paymasterVerificationGasLimit = this.paymasterVerificationGasLimit,
        paymasterPostOpGasLimit = this.paymasterPostOpGasLimit,
    )
}

internal data class UserOperationReceiptRpc(
    val actualGasCost: String?,
    val actualGasUsed: String?,
    val entryPoint: String?,
    val logs: List<LogRpc>?,
    val nonce: String?,
    val paymaster: String?,
    val reason: String?,
    val receipt: TransactionReceiptRpc,
    val sender: String?,
    val success: Boolean?,
    val userOpHash: String?,
)

internal data class LogRpc(
    val address: String?,
    val blockHash: String?,
    val blockNumber: String?,
    val data: String?,
    val logIndex: String?,
    val transactionHash: String?,
    val transactionIndex: String?,
    val removed: Boolean?,
    val topics: List<String>?,
)

internal data class TransactionReceiptRpc(
    val blobGasPrice: String?,
    val blobGasUsed: String?,
    val blockHash: String?,
    val blockNumber: String?,
    val contractAddress: String?,
    val cumulativeGasUsed: String?,
    val effectiveGasPrice: String?,
    val from: String?,
    val gasUsed: String?,
    val logs: List<LogRpc>?,
    val logsBloom: String?,
    val root: String?,
    val status: String?,
    val to: String?,
    val transactionHash: String?,
    val transactionIndex: String?,
    val type: String?,
)

internal fun LogRpc.toLog(): Log {
    return Log(
        address = this.address,
        blockHash = this.blockHash,
        blockNumber = hexToBigInteger(this.blockNumber),
        data = this.data,
        logIndex = hexToBigInteger(this.logIndex),
        transactionHash = this.transactionHash,
        transactionIndex = hexToBigInteger(this.transactionIndex),
        removed = this.removed,
        topics = this.topics,
    )
}

internal fun TransactionReceiptRpc.toTransactionReceipt(): TransactionReceipt {
    return TransactionReceipt(
        blobGasPrice = hexToBigInteger(this.blobGasPrice),
        blobGasUsed = hexToBigInteger(this.blobGasUsed),
        blockHash = this.blockHash,
        blockNumber = hexToBigInteger(this.blockNumber),
        contractAddress = this.contractAddress,
        cumulativeGasUsed = hexToBigInteger(this.cumulativeGasUsed),
        effectiveGasPrice = hexToBigInteger(this.effectiveGasPrice),
        from = this.from,
        gasUsed = hexToBigInteger(this.gasUsed),
        logs = this.logs?.map { it.toLog() },
        logsBloom = this.logsBloom,
        root = this.root,
        status = mapOf<String?, String>("0x0" to "reverted", "0x1" to "success")[this.status],
        to = this.to,
        transactionHash = this.transactionHash,
        transactionIndex = hexToBigInteger(this.transactionIndex),
        type = mapOf<String?, String>(
            "0x0" to "legacy",
            "0x1" to "eip2930",
            "0x2" to "eip1559",
            "0x3" to "eip4844",
            "0x4" to "eip7702"
        )[this.type],
    )
}

internal fun UserOperationReceiptRpc.toUserOperationReceipt(): UserOperationReceipt {
    return UserOperationReceipt(
        actualGasCost = hexToBigInteger(this.actualGasCost),
        actualGasUsed = hexToBigInteger(this.actualGasUsed),
        entryPoint = this.entryPoint,
        logs = this.logs?.map { it.toLog() },
        nonce = hexToBigInteger(this.nonce),
        paymaster = this.paymaster,
        reason = this.reason,
        receipt = this.receipt.toTransactionReceipt(),
        sender = this.sender,
        success = this.success,
        userOpHash = this.userOpHash,
    )
}

@JsonClass(generateAdapter = true)
internal data class GetUserOperationResp(
    @Json(name = "blockHash") val blockHash: String?,
    @Json(name = "blockNumber") val blockNumber: String?,
    @Json(name = "transactionHash") val transactionHash: String?,
    @Json(name = "entryPoint") val entryPoint: String?,
    @Json(name = "userOperation") val userOperation: UserOperationRpc?
)

internal fun GetUserOperationResp.toResult(): GetUserOperationResult {
    return GetUserOperationResult(
        blockHash = this.blockHash,
        blockNumber = hexToBigInteger(this.blockNumber),
        transactionHash = this.transactionHash,
        entryPoint = this.entryPoint,
        userOperation = this.userOperation
    )

}