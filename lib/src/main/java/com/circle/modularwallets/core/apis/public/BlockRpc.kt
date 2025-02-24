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

import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.models.Block
import com.circle.modularwallets.core.models.Withdrawal
import com.circle.modularwallets.core.utils.encoding.hexToBigInteger
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@ExcludeFromGeneratedCCReport
@JsonClass(generateAdapter = true)
internal data class BlockRpc(
    @Json(name = "baseFeePerGas") var baseFeePerGas: String? = null,
    @Json(name = "blobGasUsed") var blobGasUsed: String? = null,
    @Json(name = "difficulty") var difficulty: String? = null,
    @Json(name = "excessBlobGas") var excessBlobGas: String? = null,
    @Json(name = "extraData") var extraData: String? = null,
    @Json(name = "gasLimit") var gasLimit: String? = null,
    @Json(name = "gasUsed") var gasUsed: String? = null,
    @Json(name = "hash") var hash: String? = null,
    @Json(name = "logsBloom") var logsBloom: String? = null,
    @Json(name = "miner") var miner: String? = null,
    @Json(name = "mixHash") var mixHash: String? = null,
    @Json(name = "nonce") var nonce: String? = null,
    @Json(name = "number") var number: String? = null,
    @Json(name = "parentBeaconBlockRoot") var parentBeaconBlockRoot: String? = null,
    @Json(name = "parentHash") var parentHash: String? = null,
    @Json(name = "receiptsRoot") var receiptsRoot: String? = null,
    @Json(name = "sha3Uncles") var sha3Uncles: String? = null,
    @Json(name = "size") var size: String? = null,
    @Json(name = "stateRoot") var stateRoot: String? = null,
    @Json(name = "timestamp") var timestamp: String? = null,
    @Json(name = "totalDifficulty") var totalDifficulty: String? = null,
    @Json(name = "transactions") var transactions: Array<String>? = null,
    @Json(name = "transactionsRoot") var transactionsRoot: String? = null,
    @Json(name = "uncles") var uncles: Array<String>? = null,
    @Json(name = "withdrawals") var withdrawals: Array<Withdrawal>? = null,
    @Json(name = "withdrawalsRoot") var withdrawalsRoot: String? = null,
)

internal fun BlockRpc.toBlock(): Block {
    return Block(
        baseFeePerGas = hexToBigInteger(this.baseFeePerGas),
        blobGasUsed = hexToBigInteger(this.blobGasUsed),
        difficulty = hexToBigInteger(this.difficulty),
        excessBlobGas = hexToBigInteger(this.excessBlobGas),
        extraData = this.extraData,
        gasLimit = hexToBigInteger(this.gasLimit),
        gasUsed = hexToBigInteger(this.gasUsed),
        hash = this.hash,
        logsBloom = this.logsBloom,
        miner = this.miner,
        mixHash = this.mixHash,
        nonce = this.nonce,
        number = hexToBigInteger(this.number),
        parentBeaconBlockRoot = this.parentBeaconBlockRoot,
        parentHash = this.parentHash,
        receiptsRoot = this.receiptsRoot,
        sha3Uncles = this.sha3Uncles,
        size = hexToBigInteger(this.size),
        stateRoot = this.stateRoot,
        timestamp = hexToBigInteger(this.timestamp),
        totalDifficulty = hexToBigInteger(this.totalDifficulty),
        transactions = this.transactions,
        transactionsRoot = this.transactionsRoot,
        uncles = this.uncles,
        withdrawals = this.withdrawals,
        withdrawalsRoot = this.withdrawalsRoot,
    )
}
