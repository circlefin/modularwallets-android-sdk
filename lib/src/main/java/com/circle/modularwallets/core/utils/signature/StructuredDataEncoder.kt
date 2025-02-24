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

import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.models.EIP712Message
import com.circle.modularwallets.core.models.Entry
import com.fasterxml.jackson.databind.ObjectMapper
import org.web3j.abi.TypeEncoder
import org.web3j.abi.datatypes.AbiTypes
import org.web3j.crypto.Hash
import org.web3j.crypto.Pair
import org.web3j.utils.Numeric
import java.io.ByteArrayOutputStream
import java.lang.reflect.InvocationTargetException
import java.math.BigInteger
import java.util.Locale
import java.util.TreeSet
import java.util.function.Function
import java.util.regex.Pattern
import java.util.stream.Collectors

internal class StructuredDataEncoder(val jsonMessageObject: EIP712Message) {

    internal companion object {
        // Matches array declarations like arr[5][10], arr[][], arr[][34][], etc.
        // Doesn't match array declarations where there is a 0 in any dimension.
        // Eg- arr[0][5] is not matched.
        const val arrayTypeRegex: String = "^([a-zA-Z_$][a-zA-Z_$0-9]*)((\\[([1-9]\\d*)?\\])+)$"
        val arrayTypePattern: Pattern = Pattern.compile(arrayTypeRegex)

        const val bytesTypeRegex: String = "^bytes[0-9][0-9]?$"
        val bytesTypePattern: Pattern = Pattern.compile(bytesTypeRegex)

        // This regex tries to extract the dimensions from the
        // square brackets of an array declaration using the ``Regex Groups``.
        // Eg- It extracts ``5, 6, 7`` from ``[5][6][7]``
        const val arrayDimensionRegex: String = "\\[([1-9]\\d*)?\\]"
        val arrayDimensionPattern: Pattern = Pattern.compile(arrayDimensionRegex)

        // Fields of Entry Objects need to follow a regex pattern
        // Type Regex matches to a valid name or an array declaration.
        const val typeRegex: String = "^[a-zA-Z_$][a-zA-Z_$0-9]*(\\[([1-9]\\d*)*\\])*$"
        val typePattern: Pattern = Pattern.compile(typeRegex)

        // Identifier Regex matches to a valid name, but can't be an array declaration.
        const val identifierRegex: String = "^[a-zA-Z_$][a-zA-Z_$0-9]*$"
        val identifierPattern: Pattern = Pattern.compile(identifierRegex)

        fun parseJSONMessage(jsonMessageInString: String?): EIP712Message {
            val mapper = ObjectMapper()
            val tempJSONMessageObject =
                mapper.readValue(jsonMessageInString, EIP712Message::class.java)
            return tempJSONMessageObject
        }

        @Throws(java.lang.RuntimeException::class)
        fun validateStructuredData(jsonMessageObject: EIP712Message) {
            jsonMessageObject.types?.let {
                for (structName in it.keys) {
                    val fields: List<Entry> = it[structName] ?: continue
                    for ((name, type) in fields) {
                        if (name != null && !identifierPattern.matcher(name).find()) {
                            // raise Error
                            throw BaseError(
                                String.format(
                                    "Invalid Identifier %s in %s", name, structName
                                )
                            )
                        }
                        if (type != null && !typePattern.matcher(type).find()) {
                            // raise Error
                            throw BaseError(
                                String.format("Invalid Type %s in %s", type, structName)
                            )
                        }
                    }
                }
            }
        }
    }

    fun getDependencies(primaryType: String): HashSet<String> {
        // Find all the dependencies of a type
        val deps = java.util.HashSet<String>()
        val types: MutableMap<String, MutableList<Entry>> = jsonMessageObject.types ?: return deps

        if (!types.containsKey(primaryType)) {
            return deps
        }

        val remainingTypes: MutableList<String?> = ArrayList()
        remainingTypes.add(primaryType)

        while (remainingTypes.size > 0) {
            val structName = remainingTypes[remainingTypes.size - 1] ?: continue
            remainingTypes.removeAt(remainingTypes.size - 1)
            deps.add(structName)

            for ((_, type) in types[primaryType]!!) {
                if (!types.containsKey(type)) {
                    // Don't expand on non-user defined types
                } else if (deps.contains(type)) {
                    // Skip types which are already expanded
                } else {
                    // Encountered a user defined type
                    remainingTypes.add(type)
                }
            }
        }

        return deps
    }

    fun encodeStruct(structName: String): String {
        val types: MutableMap<String, MutableList<Entry>>? = jsonMessageObject.types

        var structRepresentation = StringBuilder("$structName(")
        for ((name, type) in types!![structName]!!) {
            structRepresentation.append(String.format("%s %s,", type, name))
        }
        if (!types[structName].isNullOrEmpty()) {
            structRepresentation =
                StringBuilder(
                    structRepresentation.substring(0, structRepresentation.length - 1)
                )
        }
        structRepresentation.append(")")

        return structRepresentation.toString()
    }

    fun encodeType(primaryType: String): String {
        val deps = getDependencies(primaryType)
        deps.remove(primaryType)

        // Sort the other dependencies based on Alphabetical Order and finally add the primaryType
        val depsAsList: MutableList<String> = mutableListOf()
        depsAsList.addAll(deps.toList())
        deps.sorted()
        depsAsList.add(0, primaryType)

        val result = java.lang.StringBuilder()
        for (structName in depsAsList) {
            result.append(encodeStruct(structName))
        }

        return result.toString()
    }

    fun typeHash(primaryType: String): ByteArray {
        return Numeric.hexStringToByteArray(
            Hash.sha3String(
                encodeType(
                    primaryType
                )
            )
        )
    }

    fun getArrayDimensionsFromDeclaration(declaration: String?): List<Int> {
        // Get the dimensions which were declared in Schema.
        // If any dimension is empty, then it's value is set to -1.
        val arrayTypeMatcher = arrayTypePattern.matcher(declaration ?: "")
        arrayTypeMatcher.find()
        val dimensionTypeMatcher = arrayDimensionPattern.matcher(declaration ?: "")
        val dimensions: MutableList<Int> = java.util.ArrayList()
        while (dimensionTypeMatcher.find()) {
            val currentDimension = dimensionTypeMatcher.group(1)
            if (currentDimension == null) {
                dimensions.add("-1".toInt())
            } else {
                dimensions.add(currentDimension.toInt())
            }
        }

        return dimensions
    }

    fun getDepthsAndDimensions(data: Any?, depth: Int): List<Pair> {
        if (data !is List<*>) {
            // Nothing more to recurse, since the data is no more an array
            return emptyList()
        }

        val list: MutableList<Pair> = mutableListOf()
        val dataAsArray = data.filterIsInstance<Any>()

        list.add(Pair(depth, dataAsArray.size))
        for (subdimensionalData in dataAsArray) {
            list.addAll(getDepthsAndDimensions(subdimensionalData, depth + 1))
        }

        return list
    }

    @Throws(RuntimeException::class)
    fun getArrayDimensionsFromData(data: Any?): List<Int> {
        val depthsAndDimensions = getDepthsAndDimensions(data, 0)
        // groupedByDepth has key as depth and value as List(pair(Depth, Dimension))
        val groupedByDepth =
            depthsAndDimensions.stream().collect(
                Collectors.groupingBy(
                    Function { obj: Pair -> obj.first })
            )

        // depthDimensionsMap is aimed to have key as depth and value as List(Dimension)
        val depthDimensionsMap: MutableMap<Int, List<Int>> = HashMap()
        for ((key, value) in groupedByDepth) {
            val pureDimensions: MutableList<Int> = java.util.ArrayList()
            for (depthDimensionPair in value) {
                pureDimensions.add(depthDimensionPair.second as Int)
            }
            depthDimensionsMap[key as Int] = pureDimensions
        }

        val dimensions: MutableList<Int> = java.util.ArrayList()
        for ((key, value) in depthDimensionsMap) {
            val setOfDimensionsInParticularDepth: Set<Int> = TreeSet(
                value
            )
            if (setOfDimensionsInParticularDepth.size != 1) {
                throw BaseError(
                    String.format(
                        "Depth %d of array data has more than one dimensions",
                        key
                    )
                )
            }
            dimensions.add(setOfDimensionsInParticularDepth.stream().findFirst().get())
        }

        return dimensions
    }

    fun flattenMultidimensionalArray(data: Any): List<Any> {
        if (data !is List<*>) {
            return ArrayList<Any>().apply { add(data) }
        }

        val flattenedArray = mutableListOf<Any>()
        for (arrayItem in data) {
            arrayItem?.let {
                flattenedArray.addAll(flattenMultidimensionalArray(arrayItem))
            }
        }

        return flattenedArray
    }

    private fun convertToEncodedItem(baseType: String, data: Any): ByteArray {
        var hashBytes: ByteArray
        try {
            if (baseType.lowercase(Locale.getDefault()).startsWith("uint")
                || baseType.lowercase(Locale.getDefault()).startsWith("int")
            ) {
                hashBytes = convertToBigInt(data).toByteArray()
            } else if (baseType == "string") {
                hashBytes = (data as String).toByteArray()
            } else if (baseType == "bytes") {
                hashBytes = Numeric.hexStringToByteArray(data as String)
            } else {
                val b = convertArgToBytes(data as String)
                val bi = BigInteger(1, b)
                hashBytes = Numeric.toBytesPadded(bi, 32)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            hashBytes = ByteArray(0)
        }

        return hashBytes
    }

    @Throws(NumberFormatException::class, NullPointerException::class)
    private fun convertToBigInt(value: Any): BigInteger {
        return if (value.toString().startsWith("0x")) {
            Numeric.toBigInt(value.toString())
        } else {
            BigInteger(value.toString())
        }
    }

    @Throws(java.lang.Exception::class)
    private fun convertArgToBytes(inputValue: String): ByteArray {
        var hexValue = inputValue
        if (!Numeric.containsHexPrefix(inputValue)) {
            val value = try {
                BigInteger(inputValue)
            } catch (e: java.lang.NumberFormatException) {
                BigInteger(inputValue, 16)
            }

            hexValue = Numeric.toHexStringNoPrefix(value.toByteArray())
            // fix sign condition
            if (hexValue.length > 64 && hexValue.startsWith("00")) {
                hexValue = hexValue.substring(2)
            }
        }

        return Numeric.hexStringToByteArray(hexValue)
    }

    private fun getArrayItems(field: Entry, value: Any): List<Any> {
        val expectedDimensions = getArrayDimensionsFromDeclaration(field.type)
        // This function will itself give out errors in case
        // that the data is not a proper array
        val dataDimensions = getArrayDimensionsFromData(value)

        val format = String.format(
            "Array Data %s has dimensions %s, " + "but expected dimensions are %s",
            value.toString(), dataDimensions.toString(), expectedDimensions.toString()
        )
        if (expectedDimensions.size != dataDimensions.size) {
            // Ex: Expected a 3d array, but got only a 2d array
            throw BaseError(format)
        }
        for (i in expectedDimensions.indices) {
            if (expectedDimensions[i] == -1) {
                // Skip empty or dynamically declared dimensions
                continue
            }
            if (expectedDimensions[i] != dataDimensions[i]) {
                throw BaseError(format)
            }
        }

        return flattenMultidimensionalArray(value)
    }

    @Throws(java.lang.RuntimeException::class)
    fun encodeData(primaryType: String, data: Map<String, Any>): ByteArray {
        val types: MutableMap<String, MutableList<Entry>> =
            jsonMessageObject.types ?: return ByteArrayOutputStream().toByteArray()

        val encTypes: MutableList<String> = mutableListOf()
        val encValues: MutableList<Any> = mutableListOf()

        // Add typehash
        encTypes.add("bytes32")
        encValues.add(typeHash(primaryType))

        types[primaryType]?.let {
            // Add field contents
            for (field in it) {
                val value = data[field.name] ?: continue
                val fieldType = field.type ?: continue
                if (fieldType == "string") {
                    encTypes.add("bytes32")
                    val hashedValue = Numeric.hexStringToByteArray(Hash.sha3String(value as String))
                    encValues.add(hashedValue)
                } else if (fieldType == "bytes") {
                    encTypes.add(("bytes32"))
                    encValues.add(Hash.sha3(Numeric.hexStringToByteArray(value as String)))
                } else if (types.containsKey(fieldType)) {
                    if (value is Map<*, *>) {
                        val safeMap = value.mapKeys {
                            if (it.key !is String) {
                                throw BaseError("Invalid key type: Expected String but got ${it.key?.javaClass}")
                            }
                            it.key as String
                        }.mapValues {
                            if (it.value == null) {
                                throw BaseError("Invalid value: Expected non-null Any but got null for key ${it.key}")
                            }
                            it.value as Any
                        }

                        val hashedValue = Hash.sha3(encodeData(fieldType, safeMap))
                        encTypes.add("bytes32")
                        encValues.add(hashedValue)
                    } else {
                        throw BaseError("Expected a Map<String, Any> but got ${value.javaClass}")
                    }
                } else if (bytesTypePattern.matcher(fieldType).find()) {
                    encTypes.add(fieldType)
                    encValues.add(Numeric.hexStringToByteArray(value as String))
                } else if (arrayTypePattern.matcher(fieldType).find()) {
                    val baseTypeName = fieldType.substring(0, fieldType.indexOf('['))
                    val arrayItems = getArrayItems(field, value)
                    val concatenatedArrayEncodingBuffer = ByteArrayOutputStream()

                    for (arrayItem in arrayItems) {
                        val arrayItemEncoding = if (types.containsKey(baseTypeName)) {
                            val safeMap = safeCastToMap(arrayItem)
                            Hash.sha3(encodeData(baseTypeName, safeMap))
                        } else {
                            convertToEncodedItem(
                                baseTypeName,
                                arrayItem
                            ) // add raw item, packed to 32 bytes
                        }

                        concatenatedArrayEncodingBuffer.write(
                            arrayItemEncoding, 0, arrayItemEncoding.size
                        )
                    }

                    val concatenatedArrayEncodings = concatenatedArrayEncodingBuffer.toByteArray()
                    val hashedValue = Hash.sha3(concatenatedArrayEncodings)
                    encTypes.add("bytes32")
                    encValues.add(hashedValue)
                } else if (fieldType.startsWith("uint") || fieldType.startsWith("int")) {
                    encTypes.add(fieldType)
                    // convert to BigInteger for ABI constructor compatibility
                    try {
                        encValues.add(convertToBigInt(value))
                    } catch (e: java.lang.NumberFormatException) {
                        encValues.add(
                            value
                        ) // value null or failed to convert, fallback to add string as
                        // before
                    } catch (e: java.lang.NullPointerException) {
                        encValues.add(
                            value
                        )
                    }
                } else {
                    encTypes.add(fieldType)
                    encValues.add(value)
                }
            }
        }

        val baos = ByteArrayOutputStream()
        for (i in encTypes.indices) {
            val typeClazz = AbiTypes.getType(encTypes[i])
                ?: throw BaseError("Unsupported or invalid type ${encTypes[i]}")

            var atleastOneConstructorExistsForGivenParametersType = false
            // Using the Reflection API to get the types of the parameters
            val constructors = typeClazz.constructors
            for (constructor in constructors) {
                // Check which constructor matches
                try {
                    val parameterTypes = constructor.parameterTypes
                    val temp =
                        Numeric.hexStringToByteArray(
                            TypeEncoder.encode(
                                typeClazz
                                    .getDeclaredConstructor(*parameterTypes)
                                    .newInstance(encValues[i])
                            )
                        )
                    baos.write(temp, 0, temp.size)
                    atleastOneConstructorExistsForGivenParametersType = true
                    break
                } catch (ignored: IllegalArgumentException) {
                } catch (ignored: NoSuchMethodException) {
                } catch (ignored: InstantiationException) {
                } catch (ignored: IllegalAccessException) {
                } catch (ignored: InvocationTargetException) {
                }
            }

            if (!atleastOneConstructorExistsForGivenParametersType) {
                throw BaseError(
                    String.format(
                        "Received an invalid argument for which no constructor"
                                + " exists for the ABI Class %s",
                        typeClazz.simpleName
                    )
                )
            }
        }

        return baos.toByteArray()
    }

    fun safeCastToMap(arrayItem: Any) = if (arrayItem is Map<*, *>) {
        arrayItem.mapKeys { entry ->
            if (entry.key !is String) {
                throw BaseError("Invalid key type: Expected String but got ${entry.key?.javaClass}")
            }
            entry.key as String
        }.mapValues { entry ->
            if (entry.value == null) {
                throw BaseError("Invalid value: Expected non-null Any but got null for key ${entry.key}")
            }
            entry.value as Any
        }
    } else {
        throw BaseError("Expected a Map<String, Any> but got ${arrayItem.javaClass}")
    }

    @Throws(java.lang.RuntimeException::class)
    fun hashMessage(primaryType: String, data: MutableMap<String, Any>): ByteArray {
        return Hash.sha3(encodeData(primaryType, data))
    }

    @Throws(java.lang.RuntimeException::class)
    fun hashDomain(): ByteArray {
        val data = hashMapOf<String, Any>()
        jsonMessageObject.domain?.name?.let {
            data.put("name", it)
        }
        jsonMessageObject.domain?.version?.let {
            data.put("version", it)
        }
        jsonMessageObject.domain?.chainId?.let {
            data.put("chainId", it)
        }
        jsonMessageObject.domain?.verifyingContract?.let {
            data.put("verifyingContract", it)
        }
        jsonMessageObject.domain?.salt?.let {
            data.put("salt", it)
        }
        return Hash.sha3(encodeData("EIP712Domain", data))
    }

    @Throws(java.lang.RuntimeException::class)
    fun getStructuredData(): ByteArray {
        val baos = ByteArrayOutputStream()

        val messagePrefix = "\u0019\u0001"
        val prefix = messagePrefix.toByteArray()
        baos.write(prefix, 0, prefix.size)

        val domainHash = hashDomain()
        baos.write(domainHash, 0, domainHash.size)
        if (jsonMessageObject.primaryType != "EIP712Domain" && jsonMessageObject.primaryType != null && jsonMessageObject.message != null) {
            val dataHash =
                hashMessage(
                    jsonMessageObject.primaryType!!,
                    jsonMessageObject.message!!
                )
            baos.write(dataHash, 0, dataHash.size)
        }

        return baos.toByteArray()
    }

    @Throws(java.lang.RuntimeException::class)
    fun hashStructuredData(): ByteArray {
        return Hash.sha3(getStructuredData())
    }
}


// hashStructuredData > getStructuredData > hashDomain, hashMessage
// > encodeData > typeHash > encodeType > getDependencies