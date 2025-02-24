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

package com.circle.modularwallets.core.clients

import com.circle.modularwallets.core.apis.paymaster.PaymasterApi
import com.circle.modularwallets.core.apis.paymaster.PaymasterApiImpl
import com.circle.modularwallets.core.apis.paymaster.toResult
import com.circle.modularwallets.core.chains.Chain
import com.circle.modularwallets.core.models.EntryPoint
import com.circle.modularwallets.core.models.GetPaymasterDataResult
import com.circle.modularwallets.core.models.GetPaymasterStubDataResult
import com.circle.modularwallets.core.models.IEntryPoint
import com.circle.modularwallets.core.models.UserOperation
import com.circle.modularwallets.core.transports.Transport

class PaymasterClient(chain: Chain, transport: Transport) : Client(chain, transport) {
    private val api: PaymasterApi = PaymasterApiImpl

    /**
     * Retrieves Paymaster data for a given User Operation.
     *
     * @param T The User Operation to retrieve Paymaster data for. Type T must be the subclass of UserOperation
     * @param userOp The User Operation to retrieve Paymaster data for.
     * @param entryPoint EntryPoint address to target.
     * @param context Paymaster specific fields.
     * @return Paymaster-related User Operation properties.
     */

    @Throws(Exception::class)
    @JvmOverloads
    suspend fun <T : UserOperation> getPaymasterData(
        userOp: T,
        entryPoint: EntryPoint,
        context: Map<String, Any>? = null
    ): GetPaymasterDataResult {
        return api.getPaymasterData(transport, userOp, entryPoint, chain.chainId, context)
            .toResult()
    }

    /**
     * Retrieves Paymaster stub data for a given User Operation.
     *
     * @param T The User Operation to retrieve Paymaster stub data for. Type T must be the subclass of UserOperation.
     * @param userOp The User Operation to retrieve Paymaster stub data for.
     * @param entryPoint EntryPoint address to target.
     * @param context Paymaster specific fields.
     * @return Paymaster-related User Operation properties.
     */

    @Throws(Exception::class)
    @JvmOverloads
    suspend fun <T : UserOperation> getPaymasterStubData(
        userOp: T,
        entryPoint: IEntryPoint,
        context: Map<String, Any>? = null
    ): GetPaymasterStubDataResult {
        return api.getPaymasterStubData(transport, userOp, entryPoint, chain.chainId, context)
            .toResult()
    }
}