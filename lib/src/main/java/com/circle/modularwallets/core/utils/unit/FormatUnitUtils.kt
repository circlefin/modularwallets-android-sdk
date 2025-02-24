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

import com.circle.modularwallets.core.constants.gweiUnits
import java.math.BigInteger

/**
 * Converts a numerical value in wei to a string representation in gwei.
 *
 * @param wei The numerical value in wei.
 * @param unit The unit of the value (default is "wei").
 * @return The string representation of the value in gwei.
 */
@Throws(Exception::class)
@JvmOverloads
fun formatGwei(wei: BigInteger?, unit: String = "wei"): String? {
    wei ?: return null
    val decimals = gweiUnits[unit] ?: 0
    return formatUnits(wei, decimals)
}

/**
 * Converts a numerical value in wei to a string representation in a specified unit.
 *
 * @param value The numerical value in wei.
 * @param decimals The number of decimal places to consider for the unit.
 * @return The string representation of the value in the specified unit.
 */
@Throws(Exception::class)
fun formatUnits(value: BigInteger, decimals: Int): String {

    var display = value.toString()


    val negative = display.startsWith('-')
    if (negative) display = display.substring(1)


    display = display.padStart(decimals, '0')


    val integerPart = display.substring(0, display.length - decimals)
    var fractionPart = display.substring(display.length - decimals)


    fractionPart = fractionPart.replace(Regex("0+$"), "")


    return "${if (negative) "-" else ""}${if (integerPart.isEmpty()) "0" else integerPart}" +
            if (fractionPart.isNotEmpty()) ".$fractionPart" else ""
}
