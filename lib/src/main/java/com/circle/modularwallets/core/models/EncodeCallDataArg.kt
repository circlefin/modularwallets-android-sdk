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
package com.circle.modularwallets.core.models

import com.circle.modularwallets.core.utils.abi.encodeFunctionData
import java.math.BigInteger

/**
 * Data class representing arguments required for encoding call data.
 *
 * @property to The recipient address.
 * @property value The value to be sent with the transaction.
 * @property data The call data in hexadecimal format.
 * @property abiJson The ABI definition in JSON format.
 * @property args The arguments for the function call.
 * @property functionName The function name.
 */
data class EncodeCallDataArg @JvmOverloads constructor(
    val to: String,
    val value: BigInteger? = null,
    val data: String? = null, //hex
    val abiJson: String? = null,
    val args: Array<Any>? = null,
    val functionName: String? = null
) {
    internal fun dataUpdated(): EncodeCallDataArg {
        if (!abiJson.isNullOrBlank() && !functionName.isNullOrBlank()) {
            return EncodeCallDataArg(
                to,
                value,
                encodeFunctionData(
                    functionName,
                    abiJson,
                    args ?: emptyArray()
                )
            )
        }
        return this
    }
}