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


package com.circle.modularwallets.core.errors

import com.circle.modularwallets.core.transports.JsonRpcError
import com.circle.modularwallets.core.utils.toJson


open class RpcRequestError(
    body: Any,
    error: JsonRpcError,
    url: String
) : BaseError(
    "RPC Request failed.",
    BaseErrorParameters(
        details = error.message,
        metaMessages = mutableListOf("URL: $url", "Request body: ${toJson(body)}"),
        name = "RpcRequestError"
    )
) {
    val code: Int = error.code
}

open class HttpRequestError(
    val body: Any? = null,
    cause: Throwable? = null,
    details: String? = null,
    val headers: Any? = null,
    val status: Int? = null,
    val url: String
) : BaseError(
    "HTTP request failed.",
    BaseErrorParameters(
        cause,
        details,
        metaMessages = getMetaMessage(status, url, body),
        name = "HttpRequestError"
    )
) {
    companion object {
        fun getMetaMessage(status: Int?, url: String, body: Any?): MutableList<String> {
            val result = mutableListOf<String>()
            status?.let {
                result.add("Status: $it")
            }
            result.add("URL: $url")
            body?.let {
                result.add("Request body: ${toJson(it)}")
            }
            return result
        }
    }
}

open class TimeoutError(
    body: Any?,
    url: String
) : BaseError(
    "The request took too long to respond.",
    BaseErrorParameters(
        details = "The request timed out.",
        metaMessages = mutableListOf("URL: $url", "Request body: ${toJson(body)}"),
        name = "TimeoutError"
    )
)