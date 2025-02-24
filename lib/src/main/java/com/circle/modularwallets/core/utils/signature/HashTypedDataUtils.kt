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

import com.circle.modularwallets.core.models.EIP712Domain
import com.circle.modularwallets.core.models.EIP712Message
import com.circle.modularwallets.core.models.Entry
import com.circle.modularwallets.core.utils.encoding.bytesToHex
import com.circle.modularwallets.core.utils.signature.StructuredDataEncoder.Companion.parseJSONMessage

/**
 * Hashes the given typed data using the EIP-712 standard.
 *
 * @param jsonData The JSON string representing the typed data.
 * @param overrideData Optional EIP-712 message to override the parsed JSON data.
 * @return The hashed typed data as a hex string.
 */

@JvmOverloads
@Throws(Exception::class)
fun hashTypedData(jsonData: String, overrideData: EIP712Message? = null): String {
    val typedData = parseJSONMessage(jsonData)
    overrideData?.types?.let {
        typedData.types = it
    }
    overrideData?.primaryType?.let {
        typedData.primaryType = it
    }
    overrideData?.message?.let {
        typedData.message = it
    }
    overrideData?.domain?.let {
        typedData.domain = it
    }

    typedData.types?.put("EIP712Domain", getTypesForEIP712Domain(typedData.domain))

    val dataEncoder = StructuredDataEncoder(typedData)
    val hashStructuredData = dataEncoder.hashStructuredData()
    return bytesToHex(hashStructuredData)
}

internal fun getTypesForEIP712Domain(domain: EIP712Domain?): MutableList<Entry> {
    val types = mutableListOf<Entry>()
    domain?.name?.let { types.add(Entry("name", "string")) }
    domain?.version?.let { types.add(Entry("version", "string")) }
    domain?.chainId?.let { types.add(Entry("chainId", "uint256")) }
    domain?.verifyingContract?.let { types.add(Entry("verifyingContract", "address")) }
    domain?.salt?.let { types.add(Entry("salt", "bytes32")) }
    return types
}

