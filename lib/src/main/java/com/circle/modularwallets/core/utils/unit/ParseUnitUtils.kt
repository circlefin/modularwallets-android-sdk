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


package com.circle.modularwallets.core.utils.unit

import com.circle.modularwallets.core.constants.etherUnits
import com.circle.modularwallets.core.constants.gweiUnits
import com.circle.modularwallets.core.errors.BaseError
import java.math.BigInteger
import kotlin.math.round

/**
 * Converts a string representation of gwei to numerical wei.
 *
 * @param value The string representation of gwei.
 * @param unit The unit of the value (default is "wei").
 * @return The numerical value in wei.
 * @throws IllegalArgumentException If the unit is invalid.
 * @throws BaseError If the value is not a valid decimal number.
 */
@JvmOverloads
fun parseGwei(value: String, unit: String = "wei"): BigInteger {
    val decimals = gweiUnits[unit] ?: throw BaseError("Invalid unit: $unit")
    return parseUnits(value, decimals)
}

/**
 * Converts a string representation of ether to numerical wei.
 *
 * @param value The string representation of ether.
 * @param unit The unit of the value (default is "wei").
 * @return The numerical value in wei.
 */
@JvmOverloads
@Throws(Exception::class)
fun parseEther(value: String, unit: String = "wei"): BigInteger {
    val decimals = etherUnits[unit] ?: throw BaseError("Invalid unit: $unit")
    return parseUnits(value, decimals)
}

/**
 * Multiplies a string representation of a number by a given exponent of base 10 (10^exponent).
 *
 * @param value The string representation of the number.
 * @param decimals The exponent of base 10.
 * @return The numerical value as a BigInteger.
 * @throws IllegalArgumentException If the decimals value is invalid.
 */
@Throws(Exception::class)
fun parseUnits(value: String, decimals: Int): BigInteger {
    // Validate the input string against the regex
    if (!Regex("""^(-?)([0-9]*)\.?([0-9]*)$""").matches(value)) {
        throw BaseError("InvalidDecimalNumberError $value")
    }

    // Split the input into integer and fraction parts
    var (integerPart, fractionPart) = value.split('.').let {
        it[0] to (it.getOrNull(1) ?: "0")
    }

    val negative = integerPart.startsWith('-')
    if (negative) integerPart = integerPart.substring(1)

    // Trim trailing zeros
    var fraction = fractionPart.replace(Regex("(0+)$"), "")

    // Handle case when decimals is zero
    if (decimals == 0) {
        if (round("0.$fraction".toDouble()) == 1.0) {
            val updatedInteger = BigInteger(integerPart) + BigInteger.ONE
            return BigInteger("${if (negative) "-" else ""}${updatedInteger}")
        }
        fraction = ""
    } else if (fraction.length > decimals) {
        val left = fraction.substring(0, decimals - 1)
        val unit = fraction.substring(decimals - 1, decimals)
        val right = fraction.substring(decimals)

        val rounded = round("$unit.$right".toDouble()).toInt()

        if (rounded > 9) {
            fraction =
                BigInteger(left).add(BigInteger.ONE).toString() + "0".padStart(left.length + 1, '0')
        } else {
            fraction = "$left$rounded"
        }

        if (fraction.length > decimals) {
            fraction = fraction.substring(1)
            val updatedInteger = BigInteger(integerPart) + BigInteger.ONE
            integerPart = updatedInteger.toString()
        }

        fraction = fraction.substring(0, decimals)
    } else {
        fraction = fraction.padEnd(decimals, '0')
    }

    return BigInteger("${if (negative) "-" else ""}$integerPart$fraction")
}