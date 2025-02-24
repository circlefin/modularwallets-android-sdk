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

import com.circle.modularwallets.core.errors.BaseError

/**
 * Pads the given byte array to the specified size.
 *
 * @param bytes The byte array to pad.
 * @param size The size to pad the byte array to. Default is 32.
 * @param isRight If true, padding is added to the right. If false, padding is added to the left. Default is false.
 * @return The padded byte array.
 * @throws BaseError if the size of the byte array exceeds the specified size.
 */
@JvmOverloads
@Throws(Exception::class)
fun pad(bytes: ByteArray, size: Int = 32, isRight: Boolean = false): ByteArray {
    if (bytes.size > size) {
        throw BaseError("SizeExceedsPaddingSizeError")
    }
    val paddedBytes = ByteArray(size)

    for (i in 0 until size) {
        val padEnd = isRight
        paddedBytes[if (padEnd) i else size - i - 1] =
            if (padEnd)
                bytes.getOrElse(i) { 0 }
            else
                bytes.getOrElse(bytes.size - i - 1) { 0 }
    }
    return paddedBytes
}

/**
 * Pads the given hexadecimal string to the specified size.
 *
 * @param hex The hexadecimal string to pad.
 * @param size The size to pad the hexadecimal string to. Default is 32.
 * @param isRight If true, padding is added to the right. If false, padding is added to the left. Default is false.
 * @return The padded hexadecimal string with a "0x" prefix.
 * @throws BaseError if the length of the hexadecimal string exceeds the specified size.
 */
@JvmOverloads
@Throws(Exception::class)
fun pad(hex: String?, size: Int = 32, isRight: Boolean = false): String {
    hex ?: return ""
    val hexCleaned = hex.removePrefix("0x").removePrefix("0X")
    val length = size * 2
    if (hexCleaned.length > length) {
        throw BaseError("SizeExceedsPaddingSizeError")
    }
    val result = "0x" + if (isRight) {
        hexCleaned.padEnd(length, '0')
    } else {
        hexCleaned.padStart(length, '0')
    }
    return result
}
