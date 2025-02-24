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
 * Checks if the given value is a hexadecimal string.
 *
 * @param value The value to check.
 * @param strict If true, the value must strictly match the hexadecimal pattern (starting with "0x" and followed by hexadecimal characters). If false, the value only needs to start with "0x".
 * @return True if the value is a hexadecimal string, false otherwise.
 */

@JvmOverloads
fun isHex(value: Any?, strict: Boolean = true): Boolean {
    if (value == null) return false
    if (value !is String) return false
    return if (strict) "^0x[0-9a-fA-F]*$".toRegex().matches(value) else value.startsWith("0x")
}