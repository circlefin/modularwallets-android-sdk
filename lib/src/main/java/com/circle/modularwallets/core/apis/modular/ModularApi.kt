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

import com.circle.modularwallets.core.models.AddressMappingOwner
import com.circle.modularwallets.core.models.AddressMappingResult
import com.circle.modularwallets.core.models.GetUserOperationGasPriceResult
import com.circle.modularwallets.core.transports.Transport

internal interface ModularApi {
    suspend fun getAddress(transport: Transport, getAddressReq: GetAddressReq): ModularWallet
    suspend fun createAddressMapping(
        transport: Transport,
        walletAddress: String,
        owners: Array<AddressMappingOwner>
    ): Array<AddressMappingResult>

    suspend fun getAddressMapping(
        transport: Transport,
        owner: AddressMappingOwner
    ): Array<AddressMappingResult>

    /**
     * Gets the gas price options for a user operation.
     *
     * @param transport The transport to use for the request.
     * @return The gas price options with low, medium, high tiers and optional verificationGasLimit.
     */
    suspend fun getUserOperationGasPrice(
        transport: Transport
    ): GetUserOperationGasPriceResult
}