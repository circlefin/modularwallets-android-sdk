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

import com.circle.modularwallets.core.errors.InvalidAddressError
import com.circle.modularwallets.core.models.EncodeCallDataArg
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.DynamicArray
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.DynamicStruct
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.utils.Numeric
import java.math.BigInteger

/**
 * Encodes the argument into calldata for executing a User Operation.
 *
 * @param arg The call data argument.
 * @return The encoded call data.
 */
internal fun encodeCallData(arg: EncodeCallDataArg): String {
    if (!isAddress(arg.to)) {
        throw InvalidAddressError(arg.to)
    }

    val function =
        Function(
            "execute",
            listOf<Type<*>>(
                Address(arg.to),
                Uint256(arg.value ?: BigInteger.ZERO),
                DynamicBytes(Numeric.hexStringToByteArray(arg.data ?: "0x"))
            ),
            mutableListOf(),
        )
    return FunctionEncoder.encode(function)
}

private fun isAddress(to: String): Boolean {
    val addressRegex = Regex("^0x[a-fA-F0-9]{40}$")
    return addressRegex.matches(to)
}

/**
 * Encodes the array of arguments into calldata for executing a User Operation.
 *
 * @param args The array of call data arguments.
 * @return The encoded call data.
 */
internal fun encodeCallData(args: Array<EncodeCallDataArg>): String {
    if (args.size == 1) {
        return encodeCallData(args[0])
    }
    if (args.isEmpty()) { // empty call
        return "0x34fcd5be00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000000"
    }
    val tupleArray: MutableList<DynamicStruct> = mutableListOf()
    for (arg in args) {
        tupleArray.add(
            DynamicStruct(
                Address(arg.to),
                Uint256(arg.value ?: BigInteger.ZERO),
                DynamicBytes(Numeric.hexStringToByteArray(arg.data ?: "0x"))
            )
        )
    }
    val function =
        Function(
            "executeBatch",
            listOf<Type<*>>(
                DynamicArray<DynamicStruct>(DynamicStruct::class.java, tupleArray),
            ),
            mutableListOf(),
        )
    return FunctionEncoder.encode(function)
}
