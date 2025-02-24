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

package com.circle.modularwallets.core.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigInteger

/**
 * Response model for estimating gas usage for user operations.
 *
 * @property preVerificationGas Gas overhead of this UserOperation.
 * @property verificationGasLimit Estimation of gas limit required by the validation of this UserOperation.
 * @property callGasLimit Estimation of gas limit required by the inner account execution.
 * @property paymasterVerificationGasLimit Estimation of gas limit required by the paymaster verification, if the UserOperation defines a Paymaster address.
 * @property paymasterPostOpGasLimit The amount of gas to allocate for the paymaster post-operation code.
 */
@JsonClass(generateAdapter = true)
data class EstimateUserOperationGasResult @JvmOverloads constructor(
    @Json(name = "preVerificationGas") var preVerificationGas: BigInteger? = null,
    @Json(name = "verificationGasLimit") var verificationGasLimit: BigInteger? = null,
    @Json(name = "callGasLimit") var callGasLimit: BigInteger? = null,
    @Json(name = "paymasterVerificationGasLimit") var paymasterVerificationGasLimit: BigInteger? = null,
    @Json(name = "paymasterPostOpGasLimit") var paymasterPostOpGasLimit: BigInteger? = null,
)

/**
 * Response model for estimating fees per gas.
 *
 * @property maxFeePerGas Total fee per gas in wei (gasPrice/baseFeePerGas + maxPriorityFeePerGas).
 * @property maxPriorityFeePerGas Max priority fee per gas (in wei).
 * @property gasPrice Legacy gas price (optional, usually undefined for EIP-1559).
 */
@JsonClass(generateAdapter = true)
data class EstimateFeesPerGasResult @JvmOverloads constructor(
    val maxFeePerGas: BigInteger? = null,
    val maxPriorityFeePerGas: BigInteger? = null,
    val gasPrice: BigInteger? = null,
)

/**
 * Response model for getting user operation details.
 *
 * @property blockHash The block hash the User Operation was included on.
 * @property blockNumber The block number the User Operation was included on.
 * @property entryPoint The EntryPoint which handled the User Operation.
 * @property transactionHash The hash of the transaction which included the User Operation.
 * @property userOperation The User Operation.
 */
data class GetUserOperationResult(
    val blockHash: String?,
    val blockNumber: BigInteger?,
    val transactionHash: String?,
    val entryPoint: String?,
    val userOperation: UserOperationRpc?
)

/**
 * Data class representing the receipt of a user operation.
 *
 * @property actualGasCost Actual gas cost.
 * @property actualGasUsed Actual gas used.
 * @property entryPoint Entrypoint address.
 * @property logs Logs emitted during execution.
 * @property nonce Anti-replay parameter.
 * @property paymaster Paymaster for the user operation.
 * @property reason Revert reason, if unsuccessful.
 * @property receipt Transaction receipt of the user operation execution.
 * @property sender Address of the sender.
 * @property success If the user operation execution was successful.
 * @property userOpHash Hash of the user operation.
 */
data class UserOperationReceipt(
    val actualGasCost: BigInteger?,
    val actualGasUsed: BigInteger?,
    val entryPoint: String?,
    val logs: List<Log>?,
    val nonce: BigInteger?,
    val paymaster: String?,
    val reason: String?,
    val receipt: TransactionReceipt,
    val sender: String?,
    val success: Boolean?,
    val userOpHash: String?,
)

/**
 * Data class representing a log entry.
 *
 * @property address The address from which this log originated.
 * @property blockHash Hash of the block containing this log or `null` if pending.
 * @property blockNumber Number of the block containing this log or `null` if pending.
 * @property data Contains the non-indexed arguments of the log.
 * @property logIndex Index of this log within its block or `null` if pending.
 * @property transactionHash Hash of the transaction that created this log or `null` if pending.
 * @property transactionIndex Index of the transaction that created this log or `null` if pending.
 * @property removed `true` if this filter has been destroyed and is invalid.
 * @property topics List of topics associated with this log.
 */
data class Log(
    val address: String?,
    val blockHash: String?,
    val blockNumber: BigInteger?,
    val data: String?,
    val logIndex: BigInteger?,
    val transactionHash: String?,
    val transactionIndex: BigInteger?,
    val removed: Boolean?,
    val topics: List<String>?,
)

/**
 * Data class representing the receipt of a transaction.
 *
 * @property blobGasPrice The actual value per gas deducted from the sender's account for blob gas. Only specified for blob transactions as defined by EIP-4844.
 * @property blobGasUsed The amount of blob gas used. Only specified for blob transactions as defined by EIP-4844.
 * @property blockHash Hash of the block containing this transaction.
 * @property blockNumber Number of the block containing this transaction.
 * @property contractAddress Address of the new contract or `null` if no contract was created.
 * @property cumulativeGasUsed Gas used by this and all preceding transactions in this block.
 * @property effectiveGasPrice Pre-London, it is equal to the transaction's gasPrice. Post-London, it is equal to the actual gas price paid for inclusion.
 * @property from Transaction sender.
 * @property gasUsed Gas used by this transaction.
 * @property logs List of log objects generated by this transaction.
 * @property logsBloom Logs bloom filter.
 * @property root The post-transaction state root. Only specified for transactions included before the Byzantium upgrade.
 * @property status `success` if this transaction was successful or `reverted` if it failed.
 * @property to Transaction recipient or `null` if deploying a contract.
 * @property transactionHash Hash of this transaction.
 * @property transactionIndex Index of this transaction in the block.
 * @property type Transaction type.
 */
data class TransactionReceipt(
    val blobGasPrice: BigInteger?,
    val blobGasUsed: BigInteger?,
    val blockHash: String?,
    val blockNumber: BigInteger?,
    val contractAddress: String?,
    val cumulativeGasUsed: BigInteger?,
    val effectiveGasPrice: BigInteger?,
    val from: String?,
    val gasUsed: BigInteger?,
    val logs: List<Log>?,
    val logsBloom: String?,
    val root: String?,
    val status: String?,
    val to: String?,
    val transactionHash: String?,
    val transactionIndex: BigInteger?,
    val type: String?,
)
