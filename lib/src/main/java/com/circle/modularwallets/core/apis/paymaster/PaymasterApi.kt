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

package com.circle.modularwallets.core.apis.paymaster

import com.circle.modularwallets.core.models.EntryPoint
import com.circle.modularwallets.core.models.IEntryPoint
import com.circle.modularwallets.core.models.UserOperation
import com.circle.modularwallets.core.transports.Transport

internal interface PaymasterApi {
    suspend fun <T : UserOperation> getPaymasterData(
        transport: Transport,
        userOp: T,
        entryPoint: IEntryPoint,
        chainId: Long,
        context: Map<String, Any>?
    ): GetPaymasterDataResp

    suspend fun <T : UserOperation> getPaymasterStubData(
        transport: Transport,
        userOp: T,
        entryPoint: IEntryPoint,
        chainId: Long,
        context: Map<String, Any>?
    ): GetPaymasterStubDataResp
}