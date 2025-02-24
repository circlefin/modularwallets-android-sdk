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

import com.circle.modularwallets.core.models.EncodeCallDataArg
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.math.BigInteger

/**
 * Encodes a contract execution into calldata for executing a User Operation.
 *
 * @param to The recipient address.
 * @param abiSignature The ABI signature.
 * @param args The arguments for the function call.
 * @param value The value to send with the transaction.
 * @return The encoded call data.
 */

@JvmOverloads
fun encodeContractExecution(
    to: String,
    abiSignature: String,
    args: Array<Any> = emptyArray(),
    value: BigInteger
): String {
    val encodedAbi = encodeFromAbiSignature(abiSignature, args)
    val arg = EncodeCallDataArg(to, value, encodedAbi)
    return encodeCallData(arg)
}

internal fun encodeFromAbiSignature(
    abiSignature: String,
    args: Array<Any> = emptyArray(),
): String {
    val abiDefinition = getAbiDefinitionFromAbiSignature(abiSignature)
    val encodedAbi = encodeFunctionData(abiDefinition.name, abiDefinition, args)
    return encodedAbi
}

private fun getAbiDefinitionFromAbiSignature(abiSignature: String): AbiDefinition {
    val functionName = getFunctionName(abiSignature)
    val inputs: List<AbiDefinition.NamedType> = getNamedTypesFromAbiSignature(abiSignature)
    val abiDefinition = AbiDefinition()
    abiDefinition.name = functionName
    abiDefinition.inputs = inputs
    abiDefinition.type = "function"
    abiDefinition.stateMutability = "nonpayable"

    return abiDefinition
}

private fun getNamedTypesFromAbiSignature(abiSignature: String): List<AbiDefinition.NamedType> {
    val regex = Regex("(\\w+)\\((.*)\\)")
    val matchResult = regex.matchEntire(abiSignature)

    val paramTypeString = matchResult?.groups?.get(2)?.value ?: ""
    val paramTypes =
        if (paramTypeString.isBlank()) emptyList() else parseParamTypes(paramTypeString)

    return paramTypes.map { parseType(it) }
}

private fun parseParamTypes(paramTypeString: String): List<String> {
    val paramTypes = mutableListOf<String>()
    var depth = 0
    val currentType = StringBuilder()

    for (c in paramTypeString) {
        if (c == ',' && depth == 0) {
            paramTypes.add(currentType.toString())
            currentType.setLength(0)
        } else {
            if (c == '(') {
                depth++
                currentType.append("tuple")
            }
            if (c == ')') depth--
            currentType.append(c)
        }
    }
    if (currentType.isNotEmpty()) {
        paramTypes.add(currentType.toString())
    }

    return paramTypes
}

private fun parseType(type: String): AbiDefinition.NamedType {
    return when {
        type.startsWith("tuple") -> {
            val componentsString = type.substringAfter("tuple(").substringBeforeLast(")")
            val componentTypes = parseParamTypes(componentsString)
            val components = componentTypes.map { parseType(it.trim()) }
            AbiDefinition.NamedType("", "tuple", components, "", false)
        }

        else -> {
            AbiDefinition.NamedType("", type)
        }
    }
}

private fun getFunctionName(abiSignature: String): String {
    val regex = Regex("(\\w+)\\((.*)\\)")
    val matchResult = regex.matchEntire(abiSignature)

    val functionName = matchResult?.groups?.get(1)?.value ?: ""
    return functionName
}