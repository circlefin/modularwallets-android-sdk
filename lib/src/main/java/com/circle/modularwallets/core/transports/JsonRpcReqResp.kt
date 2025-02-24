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

package com.circle.modularwallets.core.transports

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class RpcRequest @JvmOverloads constructor(
    @Json(name = "method") val method: String,
    @Json(name = "params") val params: Any? = null,
    @Json(name = "id") val id: Long = System.currentTimeMillis(),
    @Json(name = "jsonrpc") val jsonrpc: String = "2.0",
)

@Keep
@JsonClass(generateAdapter = true)
data class RpcResponse(
    @Json(name = "id") val id: String,
    @Json(name = "jsonrpc") val jsonrpc: String,
    @Json(name = "error") val error: JsonRpcError?,
    @Json(name = "result") val result: Any?,
)

@Keep
@JsonClass(generateAdapter = true)
data class JsonRpcError(
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String
)

@Keep
@JsonClass(generateAdapter = true)
data class HttpError(
    @Json(name = "statusCode") val statusCode: Int? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "message") val message: String? = null,
)