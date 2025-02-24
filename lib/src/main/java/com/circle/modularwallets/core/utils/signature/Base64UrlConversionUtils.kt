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

package com.circle.modularwallets.core.utils.signature

import android.util.Base64
import java.nio.charset.StandardCharsets

internal fun base64UrlToBase64(base64Url: String): String {
    return base64Url.replace('-', '+').replace('_', '/')
}

internal fun base64ToBase64Url(base64: String): String {
    return base64.replace('+', '-')
        .replace('/', '_')
        .replace(Regex("=+$"), "")
}

internal fun base64UrlToBytes(base64Url: String): ByteArray {
    val base64 = base64UrlToBase64(base64Url)
    return Base64.decode(base64, Base64.DEFAULT)
}

internal fun bytesToBase64Url(bytes: ByteArray): String {
    val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
    return base64ToBase64Url(base64)
}

internal fun base64DecodeToString(strEncoded: String): String {
    val decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE)
    return String(decodedBytes, StandardCharsets.UTF_8)
}

