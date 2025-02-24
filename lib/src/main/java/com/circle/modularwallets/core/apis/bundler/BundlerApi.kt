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
import com.circle.modularwallets.core.clients.BundlerClient
import com.circle.modularwallets.core.models.EncodeCallDataArg
import com.circle.modularwallets.core.models.EntryPoint
import com.circle.modularwallets.core.models.EstimateFeesPerGasResult
import com.circle.modularwallets.core.models.EstimateUserOperationGasResult
import com.circle.modularwallets.core.models.Paymaster
import com.circle.modularwallets.core.models.UserOperation
import com.circle.modularwallets.core.models.UserOperationRpc
import com.circle.modularwallets.core.models.UserOperationV07
import com.circle.modularwallets.core.transports.Transport

internal interface BundlerApi {
    suspend fun <T : UserOperation> estimateUserOperationGas(
        transport: Transport,
        userOp: T,
        entryPoint: EntryPoint
    ): EstimateUserOperationGasResult

    suspend fun getChainId(transport: Transport): String
    suspend fun getSupportedEntryPoints(transport: Transport): ArrayList<String>
    suspend fun getUserOperation(transport: Transport, userOpHash: String): GetUserOperationResp
    suspend fun getUserOperationReceipt(
        transport: Transport,
        userOpHash: String
    ): UserOperationReceiptRpc

    suspend fun prepareUserOperation(
        transport: Transport,
        account: SmartAccount,
        calls: Array<EncodeCallDataArg>?,
        partialUserOp: UserOperationV07,
        paymaster: Paymaster?,
        bundlerClient: BundlerClient,
        estimateFeesPerGas: (suspend (SmartAccount, BundlerClient, UserOperationV07) -> EstimateFeesPerGasResult)?
    ): UserOperationV07

    suspend fun sendUserOperation(
        transport: Transport,
        userOpRpc: UserOperationRpc,
        entryPointAddress: String,
    ): String

    suspend fun waitForUserOperationReceipt(
        transport: Transport,
        userOpHash: String,
        pollingInterval: Long,
        retryCount: Int,
        timeout: Long? = null
    ): UserOperationReceiptRpc
}