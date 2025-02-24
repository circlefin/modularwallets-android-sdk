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

import org.web3j.abi.TypeEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Array
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.Bytes
import org.web3j.abi.datatypes.BytesType
import org.web3j.abi.datatypes.DynamicArray
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.DynamicStruct
import org.web3j.abi.datatypes.Fixed
import org.web3j.abi.datatypes.FixedPointType
import org.web3j.abi.datatypes.NumericType
import org.web3j.abi.datatypes.StaticArray
import org.web3j.abi.datatypes.StaticStruct
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Ufixed
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.primitive.PrimitiveType
import org.web3j.utils.Numeric
import java.nio.charset.StandardCharsets

@Throws(Exception::class)
fun encodePacked(parameters: List<Type<*>>): String {
    val result = StringBuilder()
    for (parameter in parameters) {
        result.append(encodePacked(parameter))
    }
    return "0x$result"
}

private fun encodePacked(parameter: Type<*>): String {
    return when (parameter) {
        is Utf8String -> {
            Numeric.toHexStringNoPrefix(
                parameter.value.toByteArray(StandardCharsets.UTF_8)
            )
        }

        is DynamicBytes -> {
            Numeric.toHexStringNoPrefix(parameter.value)
        }

        is DynamicArray<*> -> {
            arrayEncodePacked(parameter)
        }

        is StaticArray<*> -> {
            arrayEncodePacked(parameter)
        }

        is PrimitiveType<*> -> {
            encodePacked(parameter.toSolidityType())
        }

        else -> {
            removePadding(TypeEncoder.encode(parameter), parameter)
        }
    }
}

private fun removePadding(encodedValue: String, parameter: Type<*>): String {
    when (parameter) {
        is NumericType -> {
            if (parameter is Ufixed || parameter is Fixed) {
                return encodedValue
            }
            return encodedValue.substring(64 - parameter.bitSize / 4, 64)
        }

        is Address -> {
            return encodedValue.substring(64 - parameter.toUint().bitSize / 4, 64)
        }

        is Bool -> {
            return encodedValue.substring(62, 64)
        }

        is Bytes -> {
            return encodedValue.substring(0, (parameter as BytesType).value.size * 2)
        }

        is Utf8String -> {
            val length =
                parameter.value.toByteArray(StandardCharsets.UTF_8).size
            return encodedValue.substring(64, 64 + length * 2)
        }

        is DynamicBytes -> {
            return encodedValue.substring(
                64, 64 + parameter.value.size * 2
            )
        }

        else -> {
            throw UnsupportedOperationException(
                "Type cannot be encoded: " + parameter.javaClass
            )
        }
    }
}

private fun <T : Type<*>> isSupportingEncodedPacked(value: Array<T>): Boolean {
    return !(Utf8String::class.java.isAssignableFrom(value.componentType)
            || DynamicStruct::class.java.isAssignableFrom(value.componentType)
            || DynamicArray::class.java.isAssignableFrom(value.componentType)
            || StaticStruct::class.java.isAssignableFrom(value.componentType)
            || FixedPointType::class.java.isAssignableFrom(value.componentType)
            || DynamicBytes::class.java.isAssignableFrom(value.componentType))
}

private fun <T : Type<*>> arrayEncodePacked(values: Array<T>): String {
    if (isSupportingEncodedPacked(values)) {
        if (values.value.isEmpty()) {
            return ""
        }
        if (values is DynamicArray<*>) {
            return TypeEncoder.encode(values).substring(64)
        } else if (values is StaticArray<*>) {
            return TypeEncoder.encode(values)
        }
    }
    throw UnsupportedOperationException(
        "Type cannot be packed encoded: " + values.javaClass
    )
}