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
 * Slices the given hexadecimal string from the specified start index to the end index.
 *
 * @param value The hexadecimal string to slice.
 * @param start The start index for slicing (in bytes). Default is 0.
 * @param end The end index for slicing (in bytes). Default is the length of the string divided by 2.
 * @param strict If true, the sliced value must strictly match the hexadecimal pattern (starting with "0x" and followed by hexadecimal characters). Default is false.
 * @return The sliced hexadecimal string with a "0x" prefix.
 * @throws IllegalArgumentException if the sliced value does not match the hexadecimal pattern when strict is true.
 */
@JvmOverloads
fun slice(
    value: String,
    start: Int? = null,
    end: Int? = null,
    strict: Boolean = false
): String {
    val cleanValue = value.removePrefix("0x")
    val startIndex = (start ?: 0) * 2
    val endIndex = (end ?: (cleanValue.length / 2)) * 2
    val slicedValue = "0x${cleanValue.slice(startIndex until endIndex)}"
    if (strict) {
        require(slicedValue.matches(Regex("0x[0-9a-fA-F]*"))) {
            "Invalid hexadecimal string"
        }
    }

    return slicedValue
}
