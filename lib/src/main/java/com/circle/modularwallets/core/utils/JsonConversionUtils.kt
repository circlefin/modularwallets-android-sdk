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

package com.circle.modularwallets.core.utils

import com.circle.modularwallets.core.utils.rpc.getMoshi
import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


internal fun toJson(obj: Any?): String {
    obj ?: return ""
    if (obj is String) {
        return obj
    }
    return Gson().toJson(obj)
}

internal fun <T> fromJson(jsonString: String, type: Class<T>): T? {
    val moshi = getMoshi()
    val adapter: JsonAdapter<T> = moshi.adapter(type)
    return adapter.fromJson(jsonString)
}
