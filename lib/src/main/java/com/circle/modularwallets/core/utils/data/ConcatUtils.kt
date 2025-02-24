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

package com.circle.modularwallets.core.utils.data

/**
 * Concatenates multiple strings, removing any "0x" prefixes, and returns the result with a "0x" prefix.
 *
 * @param values The strings to concatenate.
 * @return The concatenated string with a "0x" prefix.
 */
fun concat(vararg values: String): String {
    val concatenated = values.joinToString("") { it.replace("0x", "") }
    return "0x$concatenated"
}

/**
 * Concatenates multiple byte arrays into a single byte array.
 *
 * @param values The byte arrays to concatenate.
 * @return The concatenated byte array.
 */
fun concat(vararg values: ByteArray): ByteArray {
    val totalLength = values.sumOf { it.size }

    val result = ByteArray(totalLength)

    var offset = 0
    for (arr in values) {
        System.arraycopy(arr, 0, result, offset, arr.size)
        offset += arr.size
    }
    return result
}
