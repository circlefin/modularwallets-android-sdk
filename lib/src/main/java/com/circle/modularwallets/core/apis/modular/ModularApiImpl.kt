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

package com.circle.modularwallets.core.apis.modular

import com.circle.modularwallets.core.transports.RpcRequest
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.rpc.performJsonRpcRequest

internal object ModularApiImpl : ModularApi {
    override suspend fun getAddress(
        transport: Transport,
        getAddressReq: GetAddressReq
    ): ModularWallet {
        val req = RpcRequest("circle_getAddress", listOf(getAddressReq))
        val result = performJsonRpcRequest(transport, req, ModularWallet::class.java)
        return result.first
    }
}

