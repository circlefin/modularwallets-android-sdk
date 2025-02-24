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

import android.util.Log
import com.circle.modularwallets.core.BuildConfig
import org.json.JSONObject

internal object Logger {
    private const val TAG = "<Circle>"
    private val isLogEnabled: Boolean
        get() = BuildConfig.INTERNAL_BUILD

    fun sdkTag(tag: String?): String {
        return tag?.let { "$TAG $tag" } ?: TAG
    }

    fun i(tag: String? = null, msg: String, tr: Throwable? = null) {
        if (!isLogEnabled) return
        if (tr != null) {
            Log.i(sdkTag(tag), msg, tr)
        } else {
            Log.i(sdkTag(tag), msg)
        }
    }

    fun d(tag: String? = null, msg: String, tr: Throwable? = null) {
        if (!isLogEnabled) return
        if (tr != null) {
            Log.d(sdkTag(tag), msg, tr)
        } else {
            Log.d(sdkTag(tag), msg)
        }
    }

    fun w(tag: String? = null, msg: String, tr: Throwable? = null) {
        if (!isLogEnabled) return
        if (tr != null) {
            Log.w(sdkTag(tag), msg, tr)
        } else {
            Log.w(sdkTag(tag), msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun e(tag: String? = null, msg: String, tr: Throwable? = null) {
        if (!isLogEnabled) return
        if (tr != null) {
            Log.e(sdkTag(tag), msg, tr)
        } else {
            Log.e(sdkTag(tag), msg)
        }
    }
}

fun prettyPrint(args: Map<String, Any?>): String {
    val entries = args.entries
        .mapNotNull { (key, value) ->
            if (value == null || value == false) null else key to value
        }

    val maxLength = entries.fold(0) { acc, (key) ->
        maxOf(acc, key.length)
    }

    return entries.joinToString("\n") { (key, value) ->
        "  ${"${key}:".padEnd(maxLength + 1)}  $value"
    }
}

fun prettyPrintJson(obj: Any?): String {
    return JSONObject(toJson(obj)).toString(2)
}
