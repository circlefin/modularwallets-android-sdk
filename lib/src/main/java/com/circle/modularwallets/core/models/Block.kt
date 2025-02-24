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
 * Data class representing a block.
 *
 * @property baseFeePerGas Base fee per gas.
 * @property blobGasUsed Total used blob gas by all transactions in this block.
 * @property difficulty Difficulty for this block.
 * @property excessBlobGas Excess blob gas.
 * @property extraData "Extra data" field of this block.
 * @property gasLimit Maximum gas allowed in this block.
 * @property gasUsed Total used gas by all transactions in this block.
 * @property hash Block hash or `null` if pending.
 * @property logsBloom Logs bloom filter or `null` if pending.
 * @property miner Address that received this block’s mining rewards.
 * @property mixHash Unique identifier for the block.
 * @property nonce Proof-of-work hash or `null` if pending.
 * @property number Block number or `null` if pending.
 * @property parentBeaconBlockRoot Root of the parent beacon chain block.
 * @property parentHash Parent block hash.
 * @property receiptsRoot Root of this block’s receipts trie.
 * @property sealFields List of seal fields.
 * @property sha3Uncles SHA3 of the uncles data in this block.
 * @property size Size of this block in bytes.
 * @property stateRoot Root of this block’s final state trie.
 * @property timestamp Unix timestamp of when this block was collated.
 * @property totalDifficulty Total difficulty of the chain until this block.
 * @property transactions List of transaction objects or hashes.
 * @property transactionsRoot Root of this block’s transaction trie.
 * @property uncles List of uncle hashes.
 * @property withdrawals List of withdrawal objects.
 * @property withdrawalsRoot Root of this block’s withdrawals trie.
 */
@JsonClass(generateAdapter = true)
data class Block @JvmOverloads constructor(
    @Json(name = "baseFeePerGas") var baseFeePerGas: BigInteger? = null,
    @Json(name = "blobGasUsed") var blobGasUsed: BigInteger? = null,
    @Json(name = "difficulty") var difficulty: BigInteger? = null,
    @Json(name = "excessBlobGas") var excessBlobGas: BigInteger? = null,
    @Json(name = "extraData") var extraData: String? = null,
    @Json(name = "gasLimit") var gasLimit: BigInteger? = null,
    @Json(name = "gasUsed") var gasUsed: BigInteger? = null,
    @Json(name = "hash") var hash: String? = null,
    @Json(name = "logsBloom") var logsBloom: String? = null,
    @Json(name = "miner") var miner: String? = null,
    @Json(name = "mixHash") var mixHash: String? = null,
    @Json(name = "nonce") var nonce: String? = null,
    @Json(name = "number") var number: BigInteger? = null,
    @Json(name = "parentBeaconBlockRoot") var parentBeaconBlockRoot: String? = null,
    @Json(name = "parentHash") var parentHash: String? = null,
    @Json(name = "receiptsRoot") var receiptsRoot: String? = null,
    @Json(name = "sha3Uncles") var sha3Uncles: String? = null,
    @Json(name = "size") var size: BigInteger? = null,
    @Json(name = "stateRoot") var stateRoot: String? = null,
    @Json(name = "timestamp") var timestamp: BigInteger? = null,
    @Json(name = "totalDifficulty") var totalDifficulty: BigInteger? = null,
    @Json(name = "transactions") var transactions: Array<String>? = null,
    @Json(name = "transactionsRoot") var transactionsRoot: String? = null,
    @Json(name = "uncles") var uncles: Array<String>? = null,
    @Json(name = "withdrawals") var withdrawals: Array<Withdrawal>? = null,
    @Json(name = "withdrawalsRoot") var withdrawalsRoot: String? = null,
)

data class Withdrawal @JvmOverloads constructor(
    @Json(name = "index") var index: String? = null,
    @Json(name = "validatorIndex") var validatorIndex: String? = null,
    @Json(name = "address") var address: String? = null,
    @Json(name = "amount") var amount: String? = null,
)
