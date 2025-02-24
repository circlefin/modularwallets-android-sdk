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
package com.circle.modularwallets.core.transports.http

import android.content.Context
import com.circle.modularwallets.core.transports.RpcRequest
import com.circle.modularwallets.core.transports.RpcResponse
import com.circle.modularwallets.core.transports.Transport
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Data class representing HTTP RPC client options.
 *
 * @property headers Optional headers to include in the HTTP requests.
 */
data class HttpRpcClientOptions @JvmOverloads constructor(
    val headers: Map<String, String>? = null
)

interface HttpTransport : Transport {
    override suspend fun request(@Body req: RpcRequest): Response<RpcResponse>
}

internal class HttpTransportImpl(val url: String, private val service: RetrofitService) :
    HttpTransport {
    override suspend fun request(req: RpcRequest): Response<RpcResponse> {
        return service.request(url, req)
    }
}

internal interface RetrofitService {
    @POST
    suspend fun request(@Url url: String, @Body req: RpcRequest): Response<RpcResponse>
}

/**
 * Creates a HTTP transport instance.
 *
 * @param context The application context.
 * @param clientKey The client key for authorization.
 * @param url The URL for the HTTP transport.
 * @return The configured HTTP transport instance.
 */
fun toModularTransport(
    context: Context,
    clientKey: String,
    url: String
): HttpTransport {
    return http(
        context,
        url,
        HttpRpcClientOptions(headers = mapOf("Authorization" to "Bearer $clientKey"))
    )
}

/**
 * Creates a HTTP transport instance.
 *
 * @param context The application context.
 * @param clientKey The client key for authorization.
 * @param url The URL for the HTTP transport.
 * @return The configured HTTP transport instance.
 */
fun toPasskeyTransport(
    context: Context,
    clientKey: String,
    url: String
): HttpTransport {
    return http(
        context,
        url,
        HttpRpcClientOptions(headers = mapOf("Authorization" to "Bearer $clientKey"))
    )
}

/**
 * Creates an HTTP transport instance.
 *
 * @param context The application context.
 * @param url The URL for the HTTP transport.
 * @param config The configuration options for the HTTP transport (default is an empty configuration).
 * @return The configured HTTP transport instance.
 */

@JvmOverloads
fun http(
    context: Context,
    url: String,
    config: HttpRpcClientOptions = HttpRpcClientOptions()
): HttpTransport {
    val transport = RetrofitProvider().get(
        context,
        url, config
    ).create(RetrofitService::class.java)
    return HttpTransportImpl(url, transport)
}
