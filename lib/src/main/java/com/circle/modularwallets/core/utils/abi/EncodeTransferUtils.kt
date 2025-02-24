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

package com.circle.modularwallets.core.utils.abi

import com.circle.modularwallets.core.constants.ABI_ERC20
import com.circle.modularwallets.core.constants.ABI_FUNCTION_TRANSFER
import com.circle.modularwallets.core.constants.CONTRACT_ADDRESS
import com.circle.modularwallets.core.models.EncodeCallDataArg
import com.circle.modularwallets.core.models.EncodeTransferResult
import com.circle.modularwallets.core.models.Token
import java.math.BigInteger

/**
 * Encodes an ERC20 token transfer into calldata for executing a User Operation.
 *
 * @param to The recipient address.
 * @param token The token contract address or the name of [Token] enum .
 * @param amount The amount to transfer.
 * @return The encoded transfer abi and contract address.
 */
@Throws(Exception::class)
fun encodeTransfer(
    to: String,
    token: String,
    amount: BigInteger
): EncodeTransferResult {
    val abiParameters: Array<Any> = arrayOf(to, amount)
    val encodedAbi = encodeFunctionData(ABI_FUNCTION_TRANSFER, ABI_ERC20, abiParameters)
    return EncodeTransferResult(data = encodedAbi, to = CONTRACT_ADDRESS[token] ?: token)
}
