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


package com.circle.modularwallets.core.utils.rpc

import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.errors.BaseErrorParameters
import com.circle.modularwallets.core.errors.ChainDisconnectedError
import com.circle.modularwallets.core.errors.HttpRequestError
import com.circle.modularwallets.core.errors.InternalRpcError
import com.circle.modularwallets.core.errors.InvalidInputRpcError
import com.circle.modularwallets.core.errors.InvalidParamsRpcError
import com.circle.modularwallets.core.errors.InvalidRequestRpcError
import com.circle.modularwallets.core.errors.JsonRpcVersionUnsupportedError
import com.circle.modularwallets.core.errors.LimitExceededRpcError
import com.circle.modularwallets.core.errors.MethodNotFoundRpcError
import com.circle.modularwallets.core.errors.MethodNotSupportedRpcError
import com.circle.modularwallets.core.errors.ParseRpcError
import com.circle.modularwallets.core.errors.ProviderDisconnectedError
import com.circle.modularwallets.core.errors.ResourceNotFoundRpcError
import com.circle.modularwallets.core.errors.ResourceUnavailableRpcError
import com.circle.modularwallets.core.errors.RpcError
import com.circle.modularwallets.core.errors.RpcRequestError
import com.circle.modularwallets.core.errors.SwitchChainError
import com.circle.modularwallets.core.errors.TransactionRejectedRpcError
import com.circle.modularwallets.core.errors.UnauthorizedProviderError
import com.circle.modularwallets.core.errors.UnknownRpcError
import com.circle.modularwallets.core.errors.UnsupportedProviderMethodError
import com.circle.modularwallets.core.errors.UserRejectedRequestError
import com.circle.modularwallets.core.transports.HttpError
import com.circle.modularwallets.core.transports.RpcRequest
import com.circle.modularwallets.core.transports.RpcResponse
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.Logger
import com.circle.modularwallets.core.utils.fromJson
import com.circle.modularwallets.core.utils.toJson
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.math.BigDecimal
import java.math.BigInteger


internal fun <T> resultToTypeAndJson(result: Any, type: Class<T>): Pair<T, String> {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val jsonAdapter = moshi.adapter(Map::class.java)
    val jsonString = jsonAdapter.toJson(result as Map<*, *>?)
    val adapter: JsonAdapter<T> = moshi.adapter(type)
    val obj = adapter.fromJson(jsonString)
    obj ?: throw BaseError("Failed to transform from json: $jsonString")
    return Pair(obj, jsonString)
}

internal suspend fun <T> performJsonRpcRequest(
    transport: Transport,
    rpcReq: RpcRequest,
    type: Class<T>,
    notFoundError: BaseError? = null
): Pair<T, String> {
    val result = performJsonRpcRequest(transport, rpcReq, notFoundError)
    return resultToTypeAndJson(result, type)
}

internal fun getRpcError(error: RpcRequestError): RpcError {
    return when (error.code) {
        ParseRpcError.code -> ParseRpcError(error)
        InvalidRequestRpcError.code -> InvalidRequestRpcError(error)
        MethodNotFoundRpcError.code -> MethodNotFoundRpcError(error)
        InvalidParamsRpcError.code -> InvalidParamsRpcError(error)
        InternalRpcError.code -> InternalRpcError(error)
        InvalidInputRpcError.code -> InvalidInputRpcError(error)
        ResourceNotFoundRpcError.code -> ResourceNotFoundRpcError(error)
        ResourceUnavailableRpcError.code -> ResourceUnavailableRpcError(error)
        TransactionRejectedRpcError.code -> TransactionRejectedRpcError(error)
        MethodNotSupportedRpcError.code -> MethodNotSupportedRpcError(error)
        LimitExceededRpcError.code -> LimitExceededRpcError(error)
        JsonRpcVersionUnsupportedError.code -> JsonRpcVersionUnsupportedError(error)
        // CAIP-25: User Rejected Error
        // https://docs.walletconnect.com/2.0/specs/clients/sign/error-codes#rejected-caip-25
        5000, UserRejectedRequestError.code -> UserRejectedRequestError(error)
        UnauthorizedProviderError.code -> UnauthorizedProviderError(error)
        UnsupportedProviderMethodError.code -> UnsupportedProviderMethodError(error)
        ProviderDisconnectedError.code -> ProviderDisconnectedError(error)
        ChainDisconnectedError.code -> ChainDisconnectedError(error)
        SwitchChainError.code -> SwitchChainError(error)
        else -> UnknownRpcError(error)
    }
}

internal suspend fun performJsonRpcRequest(
    transport: Transport,
    rpcReq: RpcRequest,
    notFoundError: BaseError? = null
): Any {
    val call = transport.request(rpcReq)
    val body = call.body()
    body?.error?.let {
        val req = call.raw().request
        val rpcRequestError= RpcRequestError(
            getBodyString(req.body), it,
            url = req.url.toString()
        )
        throw getRpcError(rpcRequestError)
    }
    return body?.result ?: throw notFoundError ?: BaseError(
        "RPC result is null",
        BaseErrorParameters(
            metaMessages = mutableListOf(
                "URL: ${call.raw().request.url}",
                "Request body: ${toJson(getBodyString(call.raw().request.body))}"
            )
        )
    )
}

internal fun getMoshi(): Moshi {
    return Moshi.Builder()
        .add(BigDecimalAdapter)
        .add(BigIntegerAdapter)
        .add(KotlinJsonAdapterFactory())
        .build()
}

@ExcludeFromGeneratedCCReport
internal object BigDecimalAdapter {
    @FromJson
    fun fromJson(string: String) = BigDecimal(string)

    @ToJson
    fun toJson(value: BigDecimal) = value.toString()
}

@ExcludeFromGeneratedCCReport
internal object BigIntegerAdapter {
    @FromJson
    fun fromJson(string: String) = BigInteger(string)

    @ToJson
    fun toJson(value: BigInteger) = value.toString()
}

internal fun getBodyString(body: RequestBody?): String {
    val buffer = Buffer()
    body?.writeTo(buffer)
    val plainText = buffer.readByteArray()
    return String(plainText)
}

internal object ParseInterceptor : Interceptor {
    private const val MAX_RETRIES = 3
    private const val HTTP_TOO_MANY_REQUESTS = 429
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        var response: Response = chain.proceed(request)
        if (response.isSuccessful) {
            return response
        }

        var tryCount = 0
        while (tryCount < MAX_RETRIES && (response.code == HTTP_TOO_MANY_REQUESTS || response.code in 500..599)) {
            tryCount++
            Thread.sleep(1000L * tryCount)
            Logger.d("http", "Retrying request#$tryCount due to ${response.code} error")
            response = chain.proceed(chain.request())
            if (response.isSuccessful) {
                return response
            }
        }

        val responseBodyString = response.body?.string() ?: ""
        val errorMessage: String?
        try {
            if (responseBodyString.contains("jsonrpc")) {
                val data = fromJson(responseBodyString, RpcResponse::class.java)
                errorMessage = data?.error?.message
            } else {
                val data: HttpError? = fromJson(responseBodyString, HttpError::class.java)
                errorMessage = data?.error ?: data?.message
            }
        } catch (t: Throwable) {
            throw HttpRequestError(
                body = getBodyString(request.body),
                cause = t,
                details = "Receive error during parsing: $responseBodyString",
                status = response.code,
                headers = request.headers,
                url = request.url.toString()
            )
        }
        throw HttpRequestError(
            body = getBodyString(request.body), // response.body?.string() can only be called once only,
            details = errorMessage ?: "No error and no message: $responseBodyString",
            status = response.code,
            headers = request.headers,
            url = request.url.toString()
        )
    }
}
