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
import android.text.TextUtils
import android.util.Log
import com.circle.modularwallets.core.BuildConfig
import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.errors.HttpRequestError
import com.circle.modularwallets.core.errors.TimeoutError
import com.circle.modularwallets.core.utils.rpc.ParseInterceptor
import com.circle.modularwallets.core.utils.rpc.getAppInfo
import com.circle.modularwallets.core.utils.rpc.getBodyString
import com.circle.modularwallets.core.utils.rpc.getMoshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

internal class RetrofitProvider {

    fun get(context: Context, baseUrl: String, config: HttpRpcClientOptions): Retrofit {
        val splitUrl = baseUrl.split("?")
        val finalBaseUrl = if (TextUtils.isEmpty(splitUrl[0])) {
            "https://modular-sdk.circle.com/v1/rpc/w3s/buidl/" // placeholder
        } else if (splitUrl[0].endsWith("/")) {
            splitUrl[0]
        } else {
            "${splitUrl[0]}/"
        }

        val appInfo = getAppInfo(context)
        val clientBuilder = OkHttpClient.Builder()
        val requestInterceptor = Interceptor { chain ->
            val original = chain.request()
            try {
                chain.proceed(
                    original.newBuilder()
                        .apply {
                            config.headers?.let {
                                it.forEach { entry -> header(entry.key, entry.value) }
                            }
                        }
                        .header("X-AppInfo", appInfo)
                        .method(original.method, original.body)
                        .build()
                )
            } catch (e: Throwable) {
                val body = getBodyString(original.body)
                val url = original.url.toString()
                when (e) {
                    is SocketTimeoutException -> throw IOException(
                        TimeoutError(body, url)
                    )

                    is IOException -> throw e
                    is BaseError -> throw IOException(
                        e
                    )

                    else -> throw IOException(
                        HttpRequestError(
                            body = body,
                            cause = e,
                            url = url
                        )
                    )
                }
            }
        }
        clientBuilder.addInterceptor(requestInterceptor)
        clientBuilder.addInterceptor(ParseInterceptor)
        clientBuilder.readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
        val moshi = getMoshi()
        if (BuildConfig.INTERNAL_BUILD) {
            val logging =
                HttpLoggingInterceptor { message: String? -> Log.d("<Circle> Http", message!!) }
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            clientBuilder.addInterceptor(logging)
        }
        return Retrofit.Builder()
            .baseUrl(finalBaseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(clientBuilder.build())
            .build()
    }
}
