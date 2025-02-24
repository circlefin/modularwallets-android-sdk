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

import com.circle.modularwallets.core.models.Block
import com.circle.modularwallets.core.models.EstimateFeesPerGasResult
import com.circle.modularwallets.core.transports.Transport
import java.math.BigInteger

interface PublicApi {
    /** Returns the number of the most recent block seen. */
    suspend fun getBlockNum(transport: Transport): BigInteger

    /** Returns information about a block at a block number (hex) or tag. */
    suspend fun getBlock(
        transport: Transport,
        includeTransactions: Boolean = false,
        blockNumber: BigInteger
    ): Block

    /** Returns information about a block at a block number (hex) or tag. */
    suspend fun getBlock(
        transport: Transport,
        includeTransactions: Boolean = false,
        blockNumberHexOrTag: String = "latest"
    ): Block

    /** Returns the chain ID associated with the current network. */
    suspend fun getChainId(transport: Transport): String

    /** Executes a new message call immediately without submitting a transaction to the network. */
    suspend fun call(transport: Transport, from: String?, to: String, data: String): String

    /**
     * The type of fee values to return.
     * - `legacy`: Returns the legacy gas price.
     * - `eip1559`: Returns the max fee per gas and max priority fee per gas.
     * */
    suspend fun estimateFeesPerGas(
        transport: Transport,
        type: FeeValuesType = FeeValuesType.eip1559
    ): EstimateFeesPerGasResult

    /** Returns the current price of gas (in wei) */
    suspend fun getGasPrice(
        transport: Transport,
    ): BigInteger
    /**  Retrieves the bytecode at an address. */
    suspend fun getCode(transport: Transport, address: String, blockNumber: BigInteger): String
    /**  Retrieves the bytecode at an address. */
    suspend fun getCode(transport: Transport, address: String, blockNumberHexOrTag: String = "latest"): String
    /** Returns the balance of an address in wei. */
    suspend fun getBalance(transport: Transport, address: String, blockNumber: BigInteger): BigInteger
    /** Returns the balance of an address in wei. */
    suspend fun getBalance(transport: Transport, address: String, blockNumberHexOrTag: String = "latest"): BigInteger
}

enum class FeeValuesType {
    legacy,
    eip1559
}