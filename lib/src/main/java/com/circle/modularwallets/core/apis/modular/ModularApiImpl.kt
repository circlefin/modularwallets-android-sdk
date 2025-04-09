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

import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.models.AddressMappingOwner
import com.circle.modularwallets.core.models.CreateAddressMappingResult
import com.circle.modularwallets.core.models.EoaAddressMappingOwner
import com.circle.modularwallets.core.models.WebAuthnAddressMappingOwner
import com.circle.modularwallets.core.transports.RpcRequest
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.abi.isAddress
import com.circle.modularwallets.core.utils.rpc.performJsonRpcRequest
import com.circle.modularwallets.core.utils.rpc.resultToTypeAndJson

internal object ModularApiImpl : ModularApi {
    override suspend fun getAddress(
        transport: Transport,
        getAddressReq: GetAddressReq
    ): ModularWallet {
        val req = RpcRequest("circle_getAddress", listOf(getAddressReq))
        val result = performJsonRpcRequest(transport, req, ModularWallet::class.java)
        return result.first
    }

    override suspend fun createAddressMapping(
        transport: Transport,
        walletAddress: String,
        owners: Array<AddressMappingOwner>
    ): Array<CreateAddressMappingResult> {
        if (!isAddress(walletAddress)) {
            throw BaseError("walletAddress is invalid")
        }
        if (owners.isEmpty()) {
            throw BaseError("At least one owner must be provided")
        }
        owners.forEachIndexed { index, owner ->
            when (owner) {
                is EoaAddressMappingOwner -> {
                    if (!isAddress(owner.identifier.address)) {
                        throw BaseError("EOA owner at index $index has an invalid address")
                    }
                }

                is WebAuthnAddressMappingOwner -> {
                    if (owner.identifier.publicKeyX.isBlank() || owner.identifier.publicKeyY.isBlank()) {
                        throw BaseError("Webauthn owner at index $index must have publicKeyX and publicKeyY")
                    }
                }

                else -> {
                    throw BaseError("Owner at index $index has an invalid type")
                }
            }
        }

        val req = RpcRequest(
            "circle_createAddressMapping",
            listOf(CreateAddressMappingReq(walletAddress, owners))
        )
        val rawList = performJsonRpcRequest(transport, req) as ArrayList<*>
        val result: Array<CreateAddressMappingResult> = rawList.mapNotNull { item ->
            resultToTypeAndJson(item, CreateAddressMappingResult::class.java).first
        }.toTypedArray()
        return result
    }
}

