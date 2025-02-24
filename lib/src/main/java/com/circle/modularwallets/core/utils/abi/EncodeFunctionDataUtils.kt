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

import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.errors.BaseErrorParameters
import com.google.gson.Gson
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.AbiTypes
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.DynamicArray
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.DynamicStruct
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.StaticStruct
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Bytes1
import org.web3j.abi.datatypes.generated.Bytes10
import org.web3j.abi.datatypes.generated.Bytes11
import org.web3j.abi.datatypes.generated.Bytes12
import org.web3j.abi.datatypes.generated.Bytes13
import org.web3j.abi.datatypes.generated.Bytes14
import org.web3j.abi.datatypes.generated.Bytes15
import org.web3j.abi.datatypes.generated.Bytes16
import org.web3j.abi.datatypes.generated.Bytes17
import org.web3j.abi.datatypes.generated.Bytes18
import org.web3j.abi.datatypes.generated.Bytes19
import org.web3j.abi.datatypes.generated.Bytes2
import org.web3j.abi.datatypes.generated.Bytes20
import org.web3j.abi.datatypes.generated.Bytes21
import org.web3j.abi.datatypes.generated.Bytes22
import org.web3j.abi.datatypes.generated.Bytes23
import org.web3j.abi.datatypes.generated.Bytes24
import org.web3j.abi.datatypes.generated.Bytes25
import org.web3j.abi.datatypes.generated.Bytes26
import org.web3j.abi.datatypes.generated.Bytes27
import org.web3j.abi.datatypes.generated.Bytes28
import org.web3j.abi.datatypes.generated.Bytes29
import org.web3j.abi.datatypes.generated.Bytes3
import org.web3j.abi.datatypes.generated.Bytes30
import org.web3j.abi.datatypes.generated.Bytes31
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Bytes4
import org.web3j.abi.datatypes.generated.Bytes5
import org.web3j.abi.datatypes.generated.Bytes6
import org.web3j.abi.datatypes.generated.Bytes7
import org.web3j.abi.datatypes.generated.Bytes8
import org.web3j.abi.datatypes.generated.Bytes9
import org.web3j.abi.datatypes.generated.Int104
import org.web3j.abi.datatypes.generated.Int112
import org.web3j.abi.datatypes.generated.Int120
import org.web3j.abi.datatypes.generated.Int128
import org.web3j.abi.datatypes.generated.Int136
import org.web3j.abi.datatypes.generated.Int144
import org.web3j.abi.datatypes.generated.Int152
import org.web3j.abi.datatypes.generated.Int16
import org.web3j.abi.datatypes.generated.Int160
import org.web3j.abi.datatypes.generated.Int168
import org.web3j.abi.datatypes.generated.Int176
import org.web3j.abi.datatypes.generated.Int184
import org.web3j.abi.datatypes.generated.Int192
import org.web3j.abi.datatypes.generated.Int200
import org.web3j.abi.datatypes.generated.Int208
import org.web3j.abi.datatypes.generated.Int216
import org.web3j.abi.datatypes.generated.Int224
import org.web3j.abi.datatypes.generated.Int232
import org.web3j.abi.datatypes.generated.Int24
import org.web3j.abi.datatypes.generated.Int240
import org.web3j.abi.datatypes.generated.Int248
import org.web3j.abi.datatypes.generated.Int256
import org.web3j.abi.datatypes.generated.Int32
import org.web3j.abi.datatypes.generated.Int40
import org.web3j.abi.datatypes.generated.Int48
import org.web3j.abi.datatypes.generated.Int56
import org.web3j.abi.datatypes.generated.Int64
import org.web3j.abi.datatypes.generated.Int72
import org.web3j.abi.datatypes.generated.Int8
import org.web3j.abi.datatypes.generated.Int80
import org.web3j.abi.datatypes.generated.Int88
import org.web3j.abi.datatypes.generated.Int96
import org.web3j.abi.datatypes.generated.Uint104
import org.web3j.abi.datatypes.generated.Uint112
import org.web3j.abi.datatypes.generated.Uint120
import org.web3j.abi.datatypes.generated.Uint128
import org.web3j.abi.datatypes.generated.Uint136
import org.web3j.abi.datatypes.generated.Uint144
import org.web3j.abi.datatypes.generated.Uint152
import org.web3j.abi.datatypes.generated.Uint16
import org.web3j.abi.datatypes.generated.Uint160
import org.web3j.abi.datatypes.generated.Uint168
import org.web3j.abi.datatypes.generated.Uint176
import org.web3j.abi.datatypes.generated.Uint184
import org.web3j.abi.datatypes.generated.Uint192
import org.web3j.abi.datatypes.generated.Uint200
import org.web3j.abi.datatypes.generated.Uint208
import org.web3j.abi.datatypes.generated.Uint216
import org.web3j.abi.datatypes.generated.Uint224
import org.web3j.abi.datatypes.generated.Uint232
import org.web3j.abi.datatypes.generated.Uint24
import org.web3j.abi.datatypes.generated.Uint240
import org.web3j.abi.datatypes.generated.Uint248
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.datatypes.generated.Uint32
import org.web3j.abi.datatypes.generated.Uint40
import org.web3j.abi.datatypes.generated.Uint48
import org.web3j.abi.datatypes.generated.Uint56
import org.web3j.abi.datatypes.generated.Uint64
import org.web3j.abi.datatypes.generated.Uint72
import org.web3j.abi.datatypes.generated.Uint8
import org.web3j.abi.datatypes.generated.Uint80
import org.web3j.abi.datatypes.generated.Uint88
import org.web3j.abi.datatypes.generated.Uint96
import org.web3j.crypto.Hash
import org.web3j.protocol.core.methods.response.AbiDefinition
import org.web3j.utils.Numeric
import java.math.BigInteger

const val TYPE_FUNCTION: String = "function"

/**
 * Encodes the function name and parameters into an ABI encoded value (4 byte selector & arguments).
 *
 * @param functionName The function to encode from the ABI.
 * @param abiJson The ABI definition in JSON format.
 * @param args The arguments for the function call.
 * @return The encoded function call data.
 */

@Throws(Exception::class)
@JvmOverloads
fun encodeFunctionData(
    functionName: String,
    abiJson: String,
    args: Array<Any> = emptyArray()
): String {
    val abiDefinition =
        getAbiDefinition(functionName, abiJson) ?: throw BaseError("Invalid abiJson: $abiJson")
    return encodeFunctionData(functionName, abiDefinition, args)
}

internal fun encodeFunctionData(
    functionName: String,
    abiDefinition: AbiDefinition,
    args: Array<Any> = emptyArray()
): String {
    if (abiDefinition.inputs.size != args.size) {
        throw BaseError("AbiEncodingLengthMismatchError")
    }
    try {
        val finalInputs = inputFormat(abiDefinition.inputs, args)
        val finalOutputs = emptyList<TypeReference<*>>()
        val function =
            Function(
                functionName,
                finalInputs,
                finalOutputs
            )
        return encodeFunction(abiDefinition, function)
    } catch (e: Throwable) {
        if (e is BaseError) {
            throw e
        }
        throw BaseError("encode function failed", BaseErrorParameters(e))
    }
}
internal fun encodeFunction(abiDefinition: AbiDefinition, function: Function): String {
    val oriAbi = FunctionEncoder.encode(function)
    val encodedParameters = oriAbi.substring(10)
    val methodSignature = buildMethodSignature(abiDefinition)
    val methodId = buildMethodId(methodSignature)
    return "${methodId}${encodedParameters}"
}

internal fun buildMethodSignature(abiDefinition: AbiDefinition): String {
    val type = buildMethodSignatureType(abiDefinition.inputs)
    return "${abiDefinition.name}$type"
}

internal fun buildMethodSignatureType(nameTypes: List<AbiDefinition.NamedType>): String {
    val sb = StringBuilder()
    sb.append("(")
    nameTypes.forEachIndexed { index, input ->
        if (input.type.startsWith("tuple")) {
            val type = buildMethodSignatureType(input.components)
            sb.append(input.type.replace("tuple", type))
        } else {
            sb.append(input.type)
        }
        if (index != nameTypes.size - 1) {
            sb.append(",")
        }
    }
    sb.append(")")
    return sb.toString()
}

internal fun buildMethodId(methodSignature: String): String {
    val input = methodSignature.toByteArray()
    val hash = Hash.sha3(input)
    return Numeric.toHexString(hash).substring(0, 10)
}

@Throws(Exception::class)
private fun inputFormat(
    inputNameTypes: List<AbiDefinition.NamedType>,
    params: Array<Any>
): List<Type<*>> {
    val finalInputs: MutableList<Type<*>> = ArrayList()
    for (i in inputNameTypes.indices) {
        val type = inputNameTypes[i].type
        val inputType: Type<*> = when {
            type.endsWith("[]") -> {
                val elementType = type.removeSuffix("[]")
                val array = (params[i] as? Array<*>)?.map { it as Any }?.toTypedArray()
                    ?: throw BaseError("Expected an array, but got ${params[i]::class}")

                getDynamicArray(elementType, array)
            }



            type.startsWith("tuple") -> {
                getTuple(params[i])
            }

            else -> {
                getTypeInstance(type, params[i])
            }
        }
        finalInputs.add(inputType)
    }
    return finalInputs
}

internal fun getTuple(param: Any): Type<*> {
    return when (param) {
        is StaticStruct -> param
        is DynamicStruct -> param
        is List<*> -> {
            val tupleElements = param.filterIsInstance<Type<*>>()
            val hasDynamicType = param.any { it is DynamicBytes || it is Utf8String || it is DynamicArray<*> }
            if (hasDynamicType) {
                DynamicStruct(tupleElements)
            } else {
                StaticStruct(tupleElements)
            }
        }

        else -> {
            val tupleElements = (param as? Array<*>)?.mapNotNull { it as? Type<*> }
                ?: throw BaseError("Expected an array, but got ${param::class}")
            val hasDynamicType = tupleElements.any { it is DynamicBytes || it is Utf8String || it is DynamicArray<*> }
            if (hasDynamicType) {
                DynamicStruct(tupleElements)
            } else {
                StaticStruct(tupleElements)
            }
        }
    }
}

private fun getDynamicArray(type: String, param: Array<Any>): DynamicArray<*> {
    return when (type) {
        "address" -> DynamicArray(Address::class.java, param.map { Address(it.toString()) })
        "bool" -> DynamicArray(Bool::class.java, param.map { Bool(it.toString().toBoolean()) })
        "string" -> DynamicArray(Utf8String::class.java, param.map { Utf8String(it.toString()) })
        "bytes" -> DynamicArray(
            DynamicBytes::class.java,
            param.map { DynamicBytes(Numeric.hexStringToByteArray(it.toString())) })

        "uint8" -> DynamicArray(Uint8::class.java, param.map { Uint8(BigInteger(it.toString())) })
        "int8" -> DynamicArray(Int8::class.java, param.map { Int8(BigInteger(it.toString())) })
        "uint16" -> DynamicArray(
            Uint16::class.java,
            param.map { Uint16(BigInteger(it.toString())) })

        "int16" -> DynamicArray(Int16::class.java, param.map { Int16(BigInteger(it.toString())) })
        "uint24" -> DynamicArray(
            Uint24::class.java,
            param.map { Uint24(BigInteger(it.toString())) })

        "int24" -> DynamicArray(Int24::class.java, param.map { Int24(BigInteger(it.toString())) })
        "uint32" -> DynamicArray(
            Uint32::class.java,
            param.map { Uint32(BigInteger(it.toString())) })

        "int32" -> DynamicArray(Int32::class.java, param.map { Int32(BigInteger(it.toString())) })
        "uint40" -> DynamicArray(
            Uint40::class.java,
            param.map { Uint40(BigInteger(it.toString())) })

        "int40" -> DynamicArray(Int40::class.java, param.map { Int40(BigInteger(it.toString())) })
        "uint48" -> DynamicArray(
            Uint48::class.java,
            param.map { Uint48(BigInteger(it.toString())) })

        "int48" -> DynamicArray(Int48::class.java, param.map { Int48(BigInteger(it.toString())) })
        "uint56" -> DynamicArray(
            Uint56::class.java,
            param.map { Uint56(BigInteger(it.toString())) })

        "int56" -> DynamicArray(Int56::class.java, param.map { Int56(BigInteger(it.toString())) })
        "uint64" -> DynamicArray(
            Uint64::class.java,
            param.map { Uint64(BigInteger(it.toString())) })

        "int64" -> DynamicArray(Int64::class.java, param.map { Int64(BigInteger(it.toString())) })
        "uint72" -> DynamicArray(
            Uint72::class.java,
            param.map { Uint72(BigInteger(it.toString())) })

        "int72" -> DynamicArray(Int72::class.java, param.map { Int72(BigInteger(it.toString())) })
        "uint80" -> DynamicArray(
            Uint80::class.java,
            param.map { Uint80(BigInteger(it.toString())) })

        "int80" -> DynamicArray(Int80::class.java, param.map { Int80(BigInteger(it.toString())) })
        "uint88" -> DynamicArray(
            Uint88::class.java,
            param.map { Uint88(BigInteger(it.toString())) })

        "int88" -> DynamicArray(Int88::class.java, param.map { Int88(BigInteger(it.toString())) })
        "uint96" -> DynamicArray(
            Uint96::class.java,
            param.map { Uint96(BigInteger(it.toString())) })

        "int96" -> DynamicArray(Int96::class.java, param.map { Int96(BigInteger(it.toString())) })
        "uint104" -> DynamicArray(
            Uint104::class.java,
            param.map { Uint104(BigInteger(it.toString())) })

        "int104" -> DynamicArray(
            Int104::class.java,
            param.map { Int104(BigInteger(it.toString())) })

        "uint112" -> DynamicArray(
            Uint112::class.java,
            param.map { Uint112(BigInteger(it.toString())) })

        "int112" -> DynamicArray(
            Int112::class.java,
            param.map { Int112(BigInteger(it.toString())) })

        "uint120" -> DynamicArray(
            Uint120::class.java,
            param.map { Uint120(BigInteger(it.toString())) })

        "int120" -> DynamicArray(
            Int120::class.java,
            param.map { Int120(BigInteger(it.toString())) })

        "uint128" -> DynamicArray(
            Uint128::class.java,
            param.map { Uint128(BigInteger(it.toString())) })

        "int128" -> DynamicArray(
            Int128::class.java,
            param.map { Int128(BigInteger(it.toString())) })

        "uint136" -> DynamicArray(
            Uint136::class.java,
            param.map { Uint136(BigInteger(it.toString())) })

        "int136" -> DynamicArray(
            Int136::class.java,
            param.map { Int136(BigInteger(it.toString())) })

        "uint144" -> DynamicArray(
            Uint144::class.java,
            param.map { Uint144(BigInteger(it.toString())) })

        "int144" -> DynamicArray(
            Int144::class.java,
            param.map { Int144(BigInteger(it.toString())) })

        "uint152" -> DynamicArray(
            Uint152::class.java,
            param.map { Uint152(BigInteger(it.toString())) })

        "int152" -> DynamicArray(
            Int152::class.java,
            param.map { Int152(BigInteger(it.toString())) })

        "uint160" -> DynamicArray(
            Uint160::class.java,
            param.map { Uint160(BigInteger(it.toString())) })

        "int160" -> DynamicArray(
            Int160::class.java,
            param.map { Int160(BigInteger(it.toString())) })

        "uint168" -> DynamicArray(
            Uint168::class.java,
            param.map { Uint168(BigInteger(it.toString())) })

        "int168" -> DynamicArray(
            Int168::class.java,
            param.map { Int168(BigInteger(it.toString())) })

        "uint176" -> DynamicArray(
            Uint176::class.java,
            param.map { Uint176(BigInteger(it.toString())) })

        "int176" -> DynamicArray(
            Int176::class.java,
            param.map { Int176(BigInteger(it.toString())) })

        "uint184" -> DynamicArray(
            Uint184::class.java,
            param.map { Uint184(BigInteger(it.toString())) })

        "int184" -> DynamicArray(
            Int184::class.java,
            param.map { Int184(BigInteger(it.toString())) })

        "uint192" -> DynamicArray(
            Uint192::class.java,
            param.map { Uint192(BigInteger(it.toString())) })

        "int192" -> DynamicArray(
            Int192::class.java,
            param.map { Int192(BigInteger(it.toString())) })

        "uint200" -> DynamicArray(
            Uint200::class.java,
            param.map { Uint200(BigInteger(it.toString())) })

        "int200" -> DynamicArray(
            Int200::class.java,
            param.map { Int200(BigInteger(it.toString())) })

        "uint208" -> DynamicArray(
            Uint208::class.java,
            param.map { Uint208(BigInteger(it.toString())) })

        "int208" -> DynamicArray(
            Int208::class.java,
            param.map { Int208(BigInteger(it.toString())) })

        "uint216" -> DynamicArray(
            Uint216::class.java,
            param.map { Uint216(BigInteger(it.toString())) })

        "int216" -> DynamicArray(
            Int216::class.java,
            param.map { Int216(BigInteger(it.toString())) })

        "uint224" -> DynamicArray(
            Uint224::class.java,
            param.map { Uint224(BigInteger(it.toString())) })

        "int224" -> DynamicArray(
            Int224::class.java,
            param.map { Int224(BigInteger(it.toString())) })

        "uint232" -> DynamicArray(
            Uint232::class.java,
            param.map { Uint232(BigInteger(it.toString())) })

        "int232" -> DynamicArray(
            Int232::class.java,
            param.map { Int232(BigInteger(it.toString())) })

        "uint240" -> DynamicArray(
            Uint240::class.java,
            param.map { Uint240(BigInteger(it.toString())) })

        "int240" -> DynamicArray(
            Int240::class.java,
            param.map { Int240(BigInteger(it.toString())) })

        "uint248" -> DynamicArray(
            Uint248::class.java,
            param.map { Uint248(BigInteger(it.toString())) })

        "int248" -> DynamicArray(
            Int248::class.java,
            param.map { Int248(BigInteger(it.toString())) })

        "uint256" -> DynamicArray(
            Uint256::class.java,
            param.map { Uint256(BigInteger(it.toString())) })

        "int256" -> DynamicArray(
            Int256::class.java,
            param.map { Int256(BigInteger(it.toString())) })

        "bytes1" -> DynamicArray(
            Bytes1::class.java,
            param.map { Bytes1(Numeric.hexStringToByteArray(it.toString())) })

        "bytes2" -> DynamicArray(
            Bytes2::class.java,
            param.map { Bytes2(Numeric.hexStringToByteArray(it.toString())) })

        "bytes3" -> DynamicArray(
            Bytes3::class.java,
            param.map { Bytes3(Numeric.hexStringToByteArray(it.toString())) })

        "bytes4" -> DynamicArray(
            Bytes4::class.java,
            param.map { Bytes4(Numeric.hexStringToByteArray(it.toString())) })

        "bytes5" -> DynamicArray(
            Bytes5::class.java,
            param.map { Bytes5(Numeric.hexStringToByteArray(it.toString())) })

        "bytes6" -> DynamicArray(
            Bytes6::class.java,
            param.map { Bytes6(Numeric.hexStringToByteArray(it.toString())) })

        "bytes7" -> DynamicArray(
            Bytes7::class.java,
            param.map { Bytes7(Numeric.hexStringToByteArray(it.toString())) })

        "bytes8" -> DynamicArray(
            Bytes8::class.java,
            param.map { Bytes8(Numeric.hexStringToByteArray(it.toString())) })

        "bytes9" -> DynamicArray(
            Bytes9::class.java,
            param.map { Bytes9(Numeric.hexStringToByteArray(it.toString())) })

        "bytes10" -> DynamicArray(
            Bytes10::class.java,
            param.map { Bytes10(Numeric.hexStringToByteArray(it.toString())) })

        "bytes11" -> DynamicArray(
            Bytes11::class.java,
            param.map { Bytes11(Numeric.hexStringToByteArray(it.toString())) })

        "bytes12" -> DynamicArray(
            Bytes12::class.java,
            param.map { Bytes12(Numeric.hexStringToByteArray(it.toString())) })

        "bytes13" -> DynamicArray(
            Bytes13::class.java,
            param.map { Bytes13(Numeric.hexStringToByteArray(it.toString())) })

        "bytes14" -> DynamicArray(
            Bytes14::class.java,
            param.map { Bytes14(Numeric.hexStringToByteArray(it.toString())) })

        "bytes15" -> DynamicArray(
            Bytes15::class.java,
            param.map { Bytes15(Numeric.hexStringToByteArray(it.toString())) })

        "bytes16" -> DynamicArray(
            Bytes16::class.java,
            param.map { Bytes16(Numeric.hexStringToByteArray(it.toString())) })

        "bytes17" -> DynamicArray(
            Bytes17::class.java,
            param.map { Bytes17(Numeric.hexStringToByteArray(it.toString())) })

        "bytes18" -> DynamicArray(
            Bytes18::class.java,
            param.map { Bytes18(Numeric.hexStringToByteArray(it.toString())) })

        "bytes19" -> DynamicArray(
            Bytes19::class.java,
            param.map { Bytes19(Numeric.hexStringToByteArray(it.toString())) })

        "bytes20" -> DynamicArray(
            Bytes20::class.java,
            param.map { Bytes20(Numeric.hexStringToByteArray(it.toString())) })

        "bytes21" -> DynamicArray(
            Bytes21::class.java,
            param.map { Bytes21(Numeric.hexStringToByteArray(it.toString())) })

        "bytes22" -> DynamicArray(
            Bytes22::class.java,
            param.map { Bytes22(Numeric.hexStringToByteArray(it.toString())) })

        "bytes23" -> DynamicArray(
            Bytes23::class.java,
            param.map { Bytes23(Numeric.hexStringToByteArray(it.toString())) })

        "bytes24" -> DynamicArray(
            Bytes24::class.java,
            param.map { Bytes24(Numeric.hexStringToByteArray(it.toString())) })

        "bytes25" -> DynamicArray(
            Bytes25::class.java,
            param.map { Bytes25(Numeric.hexStringToByteArray(it.toString())) })

        "bytes26" -> DynamicArray(
            Bytes26::class.java,
            param.map { Bytes26(Numeric.hexStringToByteArray(it.toString())) })

        "bytes27" -> DynamicArray(
            Bytes27::class.java,
            param.map { Bytes27(Numeric.hexStringToByteArray(it.toString())) })

        "bytes28" -> DynamicArray(
            Bytes28::class.java,
            param.map { Bytes28(Numeric.hexStringToByteArray(it.toString())) })

        "bytes29" -> DynamicArray(
            Bytes29::class.java,
            param.map { Bytes29(Numeric.hexStringToByteArray(it.toString())) })

        "bytes30" -> DynamicArray(
            Bytes30::class.java,
            param.map { Bytes30(Numeric.hexStringToByteArray(it.toString())) })

        "bytes31" -> DynamicArray(
            Bytes31::class.java,
            param.map { Bytes31(Numeric.hexStringToByteArray(it.toString())) })

        "bytes32" -> DynamicArray(
            Bytes32::class.java,
            param.map { Bytes32(Numeric.hexStringToByteArray(it.toString())) })

        "tuple" -> DynamicArray(
            Type::class.java,
            param.map {
                getTuple(it)
            })

        else -> throw BaseError("Unsupported type: $type")
    }
}

private fun getTypeInstance(type: String, value: Any): Type<*> {
    return when (type) {
        "address" -> Address(value.toString())
        "bool" -> Bool(value.toString().toBoolean())
        "string" -> Utf8String(value.toString())
        "bytes" -> DynamicBytes(Numeric.hexStringToByteArray(value.toString()))
        "uint8" -> Uint8(BigInteger(value.toString()))
        "int8" -> Int8(BigInteger(value.toString()))
        "uint16" -> Uint16(BigInteger(value.toString()))
        "int16" -> Int16(BigInteger(value.toString()))
        "uint24" -> Uint24(BigInteger(value.toString()))
        "int24" -> Int24(BigInteger(value.toString()))
        "uint32" -> Uint32(BigInteger(value.toString()))
        "int32" -> Int32(BigInteger(value.toString()))
        "uint40" -> Uint40(BigInteger(value.toString()))
        "int40" -> Int40(BigInteger(value.toString()))
        "uint48" -> Uint48(BigInteger(value.toString()))
        "int48" -> Int48(BigInteger(value.toString()))
        "uint56" -> Uint56(BigInteger(value.toString()))
        "int56" -> Int56(BigInteger(value.toString()))
        "uint64" -> Uint64(BigInteger(value.toString()))
        "int64" -> Int64(BigInteger(value.toString()))
        "uint72" -> Uint72(BigInteger(value.toString()))
        "int72" -> Int72(BigInteger(value.toString()))
        "uint80" -> Uint80(BigInteger(value.toString()))
        "int80" -> Int80(BigInteger(value.toString()))
        "uint88" -> Uint88(BigInteger(value.toString()))
        "int88" -> Int88(BigInteger(value.toString()))
        "uint96" -> Uint96(BigInteger(value.toString()))
        "int96" -> Int96(BigInteger(value.toString()))
        "uint104" -> Uint104(BigInteger(value.toString()))
        "int104" -> Int104(BigInteger(value.toString()))
        "uint112" -> Uint112(BigInteger(value.toString()))
        "int112" -> Int112(BigInteger(value.toString()))
        "uint120" -> Uint120(BigInteger(value.toString()))
        "int120" -> Int120(BigInteger(value.toString()))
        "uint128" -> Uint128(BigInteger(value.toString()))
        "int128" -> Int128(BigInteger(value.toString()))
        "uint136" -> Uint136(BigInteger(value.toString()))
        "int136" -> Int136(BigInteger(value.toString()))
        "uint144" -> Uint144(BigInteger(value.toString()))
        "int144" -> Int144(BigInteger(value.toString()))
        "uint152" -> Uint152(BigInteger(value.toString()))
        "int152" -> Int152(BigInteger(value.toString()))
        "uint160" -> Uint160(BigInteger(value.toString()))
        "int160" -> Int160(BigInteger(value.toString()))
        "uint168" -> Uint168(BigInteger(value.toString()))
        "int168" -> Int168(BigInteger(value.toString()))
        "uint176" -> Uint176(BigInteger(value.toString()))
        "int176" -> Int176(BigInteger(value.toString()))
        "uint184" -> Uint184(BigInteger(value.toString()))
        "int184" -> Int184(BigInteger(value.toString()))
        "uint192" -> Uint192(BigInteger(value.toString()))
        "int192" -> Int192(BigInteger(value.toString()))
        "uint200" -> Uint200(BigInteger(value.toString()))
        "int200" -> Int200(BigInteger(value.toString()))
        "uint208" -> Uint208(BigInteger(value.toString()))
        "int208" -> Int208(BigInteger(value.toString()))
        "uint216" -> Uint216(BigInteger(value.toString()))
        "int216" -> Int216(BigInteger(value.toString()))
        "uint224" -> Uint224(BigInteger(value.toString()))
        "int224" -> Int224(BigInteger(value.toString()))
        "uint232" -> Uint232(BigInteger(value.toString()))
        "int232" -> Int232(BigInteger(value.toString()))
        "uint240" -> Uint240(BigInteger(value.toString()))
        "int240" -> Int240(BigInteger(value.toString()))
        "uint248" -> Uint248(BigInteger(value.toString()))
        "int248" -> Int248(BigInteger(value.toString()))
        "uint256" -> Uint256(BigInteger(value.toString()))
        "int256" -> Int256(BigInteger(value.toString()))
        "bytes1" -> Bytes1(Numeric.hexStringToByteArray(value.toString()))
        "bytes2" -> Bytes2(Numeric.hexStringToByteArray(value.toString()))
        "bytes3" -> Bytes3(Numeric.hexStringToByteArray(value.toString()))
        "bytes4" -> Bytes4(Numeric.hexStringToByteArray(value.toString()))
        "bytes5" -> Bytes5(Numeric.hexStringToByteArray(value.toString()))
        "bytes6" -> Bytes6(Numeric.hexStringToByteArray(value.toString()))
        "bytes7" -> Bytes7(Numeric.hexStringToByteArray(value.toString()))
        "bytes8" -> Bytes8(Numeric.hexStringToByteArray(value.toString()))
        "bytes9" -> Bytes9(Numeric.hexStringToByteArray(value.toString()))
        "bytes10" -> Bytes10(Numeric.hexStringToByteArray(value.toString()))
        "bytes11" -> Bytes11(Numeric.hexStringToByteArray(value.toString()))
        "bytes12" -> Bytes12(Numeric.hexStringToByteArray(value.toString()))
        "bytes13" -> Bytes13(Numeric.hexStringToByteArray(value.toString()))
        "bytes14" -> Bytes14(Numeric.hexStringToByteArray(value.toString()))
        "bytes15" -> Bytes15(Numeric.hexStringToByteArray(value.toString()))
        "bytes16" -> Bytes16(Numeric.hexStringToByteArray(value.toString()))
        "bytes17" -> Bytes17(Numeric.hexStringToByteArray(value.toString()))
        "bytes18" -> Bytes18(Numeric.hexStringToByteArray(value.toString()))
        "bytes19" -> Bytes19(Numeric.hexStringToByteArray(value.toString()))
        "bytes20" -> Bytes20(Numeric.hexStringToByteArray(value.toString()))
        "bytes21" -> Bytes21(Numeric.hexStringToByteArray(value.toString()))
        "bytes22" -> Bytes22(Numeric.hexStringToByteArray(value.toString()))
        "bytes23" -> Bytes23(Numeric.hexStringToByteArray(value.toString()))
        "bytes24" -> Bytes24(Numeric.hexStringToByteArray(value.toString()))
        "bytes25" -> Bytes25(Numeric.hexStringToByteArray(value.toString()))
        "bytes26" -> Bytes26(Numeric.hexStringToByteArray(value.toString()))
        "bytes27" -> Bytes27(Numeric.hexStringToByteArray(value.toString()))
        "bytes28" -> Bytes28(Numeric.hexStringToByteArray(value.toString()))
        "bytes29" -> Bytes29(Numeric.hexStringToByteArray(value.toString()))
        "bytes30" -> Bytes30(Numeric.hexStringToByteArray(value.toString()))
        "bytes31" -> Bytes31(Numeric.hexStringToByteArray(value.toString()))
        "bytes32" -> Bytes32(Numeric.hexStringToByteArray(value.toString()))
        else -> throw BaseError("Unsupported type: $type")
    }
}

private fun getAbiDefinition(name: String, contractAbi: String?): AbiDefinition? {
    val abiDefinitions = Gson().fromJson(contractAbi, Array<AbiDefinition>::class.java)
        ?: return null
    var result: AbiDefinition? = null
    for (abiDefinition in abiDefinitions) {
        if (TYPE_FUNCTION == abiDefinition.type && name == abiDefinition.name) {
            result = abiDefinition
            break
        }
    }
    return result
}

