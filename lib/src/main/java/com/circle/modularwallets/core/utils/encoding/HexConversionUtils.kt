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

package com.circle.modularwallets.core.utils.encoding

import com.circle.modularwallets.core.errors.IntegerOutOfRangeError
import java.math.BigInteger

/**
 * Converts a BigInteger to a hexadecimal string with a "0x" prefix.
 *
 * @param num The BigInteger to convert.
 * @return The hexadecimal string representation of the BigInteger with a "0x" prefix, or null if the input is null.
 */
fun bigIntegerToHex(num: BigInteger?): String? {
    num ?: return null
    // Check num MUST NOT be negative
    if (num.signum() < 0) {
        throw IntegerOutOfRangeError()
    }
    return "0x${num.toString(16)}"
}

/**
 * Converts a hexadecimal string to a BigInteger.
 *
 * @param hex The hexadecimal string to convert.
 * @return The BigInteger representation of the hexadecimal string, or null if the input is null.
 */
fun hexToBigInteger(hex: String?): BigInteger? {
    hex ?: return null
    return BigInteger(hex.removePrefix("0x").removePrefix("0X"), 16)
}

/**
 * Converts a string to its hexadecimal string representation with a "0x" prefix.
 *
 * @param string The string to convert.
 * @return The hexadecimal string representation of the input string with a "0x" prefix.
 */
fun stringToHex(string: String): String {
    return bytesToHex(string.toByteArray())
}

/**
 * Converts a byte array to its hexadecimal string representation with a "0x" prefix.
 *
 * @param bytes The byte array to convert.
 * @return The hexadecimal string representation of the byte array with a "0x" prefix.
 */
fun bytesToHex(bytes: ByteArray): String {
    return "0x${bytes.joinToString("") { String.format("%02x", it) }}"
}

/**
 * Converts a hexadecimal string to a Long.
 *
 * @param hex The hexadecimal string to convert.
 * @return The Long representation of the hexadecimal string.
 */
fun hexToLong(hex: String): Long {
    return hex.removePrefix("0x").removePrefix("0X").toLong(16)
}
