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

import com.circle.modularwallets.core.accounts.SmartAccount
import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.apis.paymaster.PaymasterApiImpl
import com.circle.modularwallets.core.apis.public.FeeValuesType
import com.circle.modularwallets.core.apis.public.PublicApi
import com.circle.modularwallets.core.apis.public.PublicApiImpl
import com.circle.modularwallets.core.clients.BundlerClient
import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.errors.BaseErrorParameters
import com.circle.modularwallets.core.errors.UserOperationReceiptNotFoundError
import com.circle.modularwallets.core.errors.WaitForUserOperationReceiptTimeoutError
import com.circle.modularwallets.core.models.EncodeCallDataArg
import com.circle.modularwallets.core.models.EntryPoint
import com.circle.modularwallets.core.models.EstimateFeesPerGasResult
import com.circle.modularwallets.core.models.EstimateUserOperationGasResult
import com.circle.modularwallets.core.models.Paymaster
import com.circle.modularwallets.core.models.UserOperation
import com.circle.modularwallets.core.models.UserOperationRpc
import com.circle.modularwallets.core.models.UserOperationV07
import com.circle.modularwallets.core.models.toRpcUserOperation
import com.circle.modularwallets.core.transports.RpcRequest
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.Logger
import com.circle.modularwallets.core.utils.error.getUserOperationError
import com.circle.modularwallets.core.utils.rpc.performJsonRpcRequest
import com.circle.modularwallets.core.utils.unit.parseGwei
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.math.BigInteger

internal object BundlerApiImpl : BundlerApi {
    private val publicApi: PublicApi = PublicApiImpl
    override suspend fun <T : UserOperation> estimateUserOperationGas(
        transport: Transport,
        userOp: T,
        entryPoint: EntryPoint
    ): EstimateUserOperationGasResult {
        val userOpRpc =
            if (userOp is UserOperationV07) userOp.toRpcUserOperation() else userOp.toRpcUserOperation()
        try {
            val req = RpcRequest(
                "eth_estimateUserOperationGas",
                listOf<Any>(userOpRpc, entryPoint.address)
            )
            val result =
                performJsonRpcRequest(transport, req, EstimateUserOperationGasResp::class.java)
            return result.first.toResult()
        } catch (e: Throwable) {
            if (e is BaseError) {
                throw getUserOperationError(e, userOpRpc)
            }
            throw getUserOperationError(
                BaseError(e.message ?: "", BaseErrorParameters(cause = e)),
                userOpRpc
            )
        }
    }

    override suspend fun getChainId(transport: Transport): String {
        return publicApi.getChainId(transport)
    }

    override suspend fun getSupportedEntryPoints(transport: Transport): ArrayList<String> {
        val req = RpcRequest("eth_supportedEntryPoints")
        val r = performJsonRpcRequest(transport, req)
        return when (r) {
            is String -> arrayListOf(r)
            is List<*> -> r.filterIsInstance<String>().takeIf { it.isNotEmpty() }?.let(::ArrayList)
            else -> null
        } ?: throw BaseError("getSupportedEntryPoints failed while casting")
    }

    override suspend fun waitForUserOperationReceipt(
        transport: Transport,
        userOpHash: String,
        pollingInterval: Long,
        retryCount: Int,
        timeout: Long?
    ): UserOperationReceiptRpc {
        try{
            if (timeout != null) {
                return withTimeout(timeout) {
                    return@withTimeout startPolling(pollingInterval, retryCount) {
                        try {
                            return@startPolling getUserOperationReceipt(transport, userOpHash)
                        } catch (e: UserOperationReceiptNotFoundError) {
                            return@startPolling null
                        }
                    }
                } ?: throw WaitForUserOperationReceiptTimeoutError(userOpHash)
            } else {
                return startPolling(pollingInterval, retryCount) {
                    try {
                        return@startPolling getUserOperationReceipt(transport, userOpHash)
                    } catch (e: UserOperationReceiptNotFoundError) {
                        return@startPolling null
                    }
                } ?: throw WaitForUserOperationReceiptTimeoutError(userOpHash)
            }
        } catch (e: TimeoutCancellationException){
            throw WaitForUserOperationReceiptTimeoutError(userOpHash)
        }
    }

    override suspend fun getUserOperation(
        transport: Transport,
        userOpHash: String
    ): GetUserOperationResp {
        val req = RpcRequest("eth_getUserOperationByHash", listOf<Any>(userOpHash))
        val result = performJsonRpcRequest(
            transport,
            req,
            GetUserOperationResp::class.java,
            UserOperationReceiptNotFoundError(userOpHash)
        )
        return result.first
    }

    override suspend fun getUserOperationReceipt(
        transport: Transport,
        userOpHash: String
    ): UserOperationReceiptRpc {
        val req = RpcRequest("eth_getUserOperationReceipt", listOf<Any>(userOpHash))
        val result = performJsonRpcRequest(
            transport,
            req,
            UserOperationReceiptRpc::class.java,
            UserOperationReceiptNotFoundError(userOpHash)
        )
        return result.first
    }

    override suspend fun prepareUserOperation(
        transport: Transport,
        account: SmartAccount,
        calls: Array<EncodeCallDataArg>?,
        partialUserOp: UserOperationV07,
        paymaster: Paymaster?,
        bundlerClient: BundlerClient,
        estimateFeesPerGas: (suspend (SmartAccount, BundlerClient, UserOperationV07) -> EstimateFeesPerGasResult)?
    ): UserOperationV07 {
        account.userOperation?.estimateGas?.let {
            val r = it.invoke(partialUserOp)
            r.preVerificationGas?.let {
                partialUserOp.preVerificationGas = it
            }
            r.verificationGasLimit?.let {
                partialUserOp.verificationGasLimit = it
            }
            r.callGasLimit?.let {
                partialUserOp.callGasLimit = it
            }
            r.paymasterVerificationGasLimit?.let {
                partialUserOp.paymasterVerificationGasLimit = it
            }
            r.paymasterPostOpGasLimit?.let {
                partialUserOp.paymasterPostOpGasLimit = it
            }
        }

        val userOp = partialUserOp.copy()
        userOp.sender = account.getAddress()

        calls?.let {
            val updatedCalls = getUpdatedCalls(it)
            userOp.callData = account.encodeCalls(updatedCalls)
        }

        if (partialUserOp.factory.isNullOrBlank() or partialUserOp.factoryData.isNullOrBlank()) {
            val arg = account.getFactoryArgs()
            arg?.let {
                userOp.factory = arg.first
                userOp.factoryData = arg.second
            }
        }
        try {
            if (partialUserOp.maxFeePerGas == null || partialUserOp.maxPriorityFeePerGas == null) {
                if (estimateFeesPerGas == null) {
                    val defaultMaxFeePerGas = parseGwei("3")
                    val defaultMaxPriorityFeePerGas = parseGwei("1")
                    val two = BigInteger.valueOf(2)
                    val fees = publicApi.estimateFeesPerGas(
                        account.client.transport,
                        FeeValuesType.eip1559
                    )

                    if (partialUserOp.maxFeePerGas == null) {
                        userOp.maxFeePerGas = defaultMaxFeePerGas
                        fees.maxFeePerGas?.let {
                            userOp.maxFeePerGas = defaultMaxFeePerGas.max(it.multiply(two))
                        }
                    }
                    if (partialUserOp.maxPriorityFeePerGas == null) {
                        userOp.maxPriorityFeePerGas = defaultMaxPriorityFeePerGas
                        fees.maxPriorityFeePerGas?.let {
                            userOp.maxPriorityFeePerGas =
                                defaultMaxPriorityFeePerGas.max(it.multiply(two))
                        }
                    }
                } else {
                    val r = estimateFeesPerGas(account, bundlerClient, userOp)
                    if (partialUserOp.maxFeePerGas == null) {
                        userOp.maxFeePerGas = r.maxFeePerGas
                    }
                    if (partialUserOp.maxPriorityFeePerGas == null) {
                        userOp.maxPriorityFeePerGas = r.maxPriorityFeePerGas
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        if (partialUserOp.nonce == null) {
            userOp.nonce = account.getNonce()
        }
        if (partialUserOp.signature.isNullOrBlank()) {
            userOp.signature = account.getStubSignature(partialUserOp)
        }
        var isPaymasterPopulated = false
        if (paymaster != null) {
            val stubR = when (paymaster) {
                is Paymaster.True -> {
                    PaymasterApiImpl.getPaymasterStubData(
                        transport,
                        userOp,
                        account.entryPoint,
                        bundlerClient.chain.chainId,
                        paymaster.paymasterContext
                    )
                }

                is Paymaster.Client -> {
                    PaymasterApiImpl.getPaymasterStubData(
                        paymaster.client.transport,
                        userOp,
                        account.entryPoint,
                        bundlerClient.chain.chainId,
                        paymaster.paymasterContext
                    )
                }
            }


            isPaymasterPopulated = stubR.isFinal
            userOp.paymaster = stubR.paymaster
            userOp.paymasterVerificationGasLimit = stubR.paymasterVerificationGasLimit
            userOp.paymasterPostOpGasLimit = stubR.paymasterPostOpGasLimit
            userOp.paymasterData = stubR.paymasterData
        }

        // If not all the gas properties are already populated, we will need to estimate the gas
        // to fill the gas properties.
        if (userOp.preVerificationGas == null ||
            userOp.verificationGasLimit == null ||
            userOp.callGasLimit == null ||
            (paymaster != null && userOp.paymasterVerificationGasLimit == null) ||
            (paymaster != null && userOp.paymasterPostOpGasLimit == null)
        ) {
            // Some Bundlers fail if nullish gas values are provided for gas estimation :') –
            // so we will need to set a default zeroish value.
            val tmpUserOp = userOp.copy()
            tmpUserOp.callGasLimit = BigInteger.ZERO
            tmpUserOp.preVerificationGas = BigInteger.ZERO
            if(paymaster != null){
                tmpUserOp.paymasterVerificationGasLimit = BigInteger.ZERO
                tmpUserOp.paymasterPostOpGasLimit = BigInteger.ZERO
            } else{
                tmpUserOp.paymasterVerificationGasLimit = null
                tmpUserOp.paymasterPostOpGasLimit = null
            }
            val r = estimateUserOperationGas(transport, tmpUserOp, account.entryPoint)
            userOp.callGasLimit =
                if (userOp.callGasLimit == null) r.callGasLimit else userOp.callGasLimit
            userOp.preVerificationGas =
                if (userOp.preVerificationGas == null) r.preVerificationGas else userOp.preVerificationGas
            userOp.verificationGasLimit =
                if (userOp.verificationGasLimit == null) r.verificationGasLimit else userOp.verificationGasLimit
            userOp.paymasterPostOpGasLimit =
                if (userOp.paymasterPostOpGasLimit == null) r.paymasterPostOpGasLimit else userOp.paymasterPostOpGasLimit
            userOp.paymasterVerificationGasLimit =
                if (userOp.paymasterVerificationGasLimit == null) r.paymasterVerificationGasLimit else userOp.paymasterVerificationGasLimit
        }
        if (paymaster != null && !isPaymasterPopulated) {
            val r = when (paymaster) {
                is Paymaster.True -> {
                    PaymasterApiImpl.getPaymasterData(
                        transport,
                        userOp,
                        account.entryPoint,
                        bundlerClient.chain.chainId,
                        paymaster.paymasterContext
                    )
                }

                is Paymaster.Client -> {
                    PaymasterApiImpl.getPaymasterData(
                        paymaster.client.transport,
                        userOp,
                        account.entryPoint,
                        bundlerClient.chain.chainId,
                        paymaster.paymasterContext
                    )
                }
            }
            userOp.paymaster = r.paymaster
            userOp.paymasterData = r.paymasterData
        }
        return userOp
    }

    @ExcludeFromGeneratedCCReport
    override suspend fun sendUserOperation(
        transport: Transport,
        userOpRpc: UserOperationRpc,
        entryPointAddress: String,
    ): String {
        try {
            val req = RpcRequest("eth_sendUserOperation", listOf(userOpRpc, entryPointAddress))
            val result = performJsonRpcRequest(transport, req) as String
            return result
        } catch (e: Throwable) {
            if (e is BaseError) {
                throw getUserOperationError(e, userOpRpc)
            }
            throw getUserOperationError(
                BaseError(e.message ?: "", BaseErrorParameters(cause = e)),
                userOpRpc
            )
        }
    }
}
internal fun getUpdatedCalls(calls: Array<EncodeCallDataArg>): Array<EncodeCallDataArg>{
    val updatedCalls: Array<EncodeCallDataArg> = calls.map { call ->
        return@map call.dataUpdated()
    }.toTypedArray()
    return updatedCalls
}
internal suspend fun <T> startPolling(
    pollingInterval: Long,
    retryCount: Int,
    block: suspend () -> T
): T? {
    var currentCount = 0
    while (currentCount < retryCount) {
        Logger.i("startPolling", "Polling currentCount: $currentCount")
        val result = block()
        if (result != null) {
            Logger.i("startPolling", "Polling got result: $currentCount")
            return result
        }
        currentCount++
        delay(pollingInterval)
    }
    Logger.i("startPolling", "Polling no result")
    return null
}