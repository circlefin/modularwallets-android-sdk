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

import com.circle.modularwallets.core.constants.REPLAY_SAFE_HASH_V1
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EIP712Message @JvmOverloads constructor(
    @Json(name = "types") var types: MutableMap<String, MutableList<Entry>>? = null,
    @Json(name = "primaryType") var primaryType: String? = null,
    @Json(name = "message") var message: MutableMap<String, Any>? = null,
    @Json(name = "domain") var domain: EIP712Domain? = null,
)

@JsonClass(generateAdapter = true)
data class Entry @JvmOverloads constructor(
    @Json(name = "name") var name: String? = null,
    @Json(name = "type") var type: String? = null,
)

@JsonClass(generateAdapter = true)
data class EIP712Domain @JvmOverloads constructor(
    @Json(name = "name") var name: String? = null,
    @Json(name = "version") var version: String? = null,
    @Json(name = "chainId") var chainId: Long? = null,
    @Json(name = "verifyingContract") val verifyingContract: String? = null,
    @Json(name = "salt") var salt: String? = null,
)

internal fun getCircleDomainMessage(chainId: Long, verifyingContract: String, hash: String): EIP712Message {
    return EIP712Message(
        domain = EIP712Domain(
            REPLAY_SAFE_HASH_V1.name,
            REPLAY_SAFE_HASH_V1.version,
            chainId,
            verifyingContract
        ),
        types = mutableMapOf(
            REPLAY_SAFE_HASH_V1.primaryType to mutableListOf(
                Entry(
                    "hash",
                    "bytes32"
                )
            )
        ),
        primaryType = REPLAY_SAFE_HASH_V1.primaryType,
        message = mutableMapOf("hash" to hash)
    )
}
