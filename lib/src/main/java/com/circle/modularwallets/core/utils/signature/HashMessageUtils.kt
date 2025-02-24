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

import com.circle.modularwallets.core.utils.encoding.bytesToHex
import com.circle.modularwallets.core.utils.encoding.toBytes
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric

/**
 * Hashes the given hex string using Ethereum's message hash algorithm.
 *
 * @param hex The hex string to hash.
 * @return The hashed message as a hex string.
 */
fun hashMessage(hex: String): String {
    val bytes = toBytes(hex)
    return hashMessage(bytes)
}

/**
 * Hashes the given byte array using Ethereum's message hash algorithm.
 *
 * @param byteArray The byte array to hash.
 * @return The hashed message as a hex string.
 */
fun hashMessage(byteArray: ByteArray): String {
    return bytesToHex(Sign.getEthereumMessageHash(byteArray))
}
