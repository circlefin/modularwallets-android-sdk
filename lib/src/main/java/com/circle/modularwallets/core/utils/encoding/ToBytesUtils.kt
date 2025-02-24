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

import com.circle.modularwallets.core.utils.data.isHex
import org.web3j.crypto.Hash
import org.web3j.utils.Numeric
import java.nio.charset.StandardCharsets

/**
 * Converts a string to a byte array.
 *
 * @param value The string to convert. If the string is a hexadecimal string, it will be converted to a byte array accordingly.
 * @return The byte array representation of the input string.
 */
fun toBytes(value: String): ByteArray {
    if(isHex(value)) {
        return Numeric.hexStringToByteArray(value)
    }
    return value.toByteArray()
}

/**
 * Computes the SHA-3 (Keccak-256) hash of the given message and returns it as a byte array.
 *
 * @param message The message to hash. If the message is a hexadecimal string, it will be converted to a byte array before hashing.
 * @return The SHA-3 hash of the input message as a byte array.
 */
fun toSha3Bytes(message: String): ByteArray {
    val digest = if(isHex(message)) {
        Hash.sha3(Numeric.hexStringToByteArray(message))
    } else {
        Hash.sha3(message.toByteArray(StandardCharsets.UTF_8))
    }
    return digest
}