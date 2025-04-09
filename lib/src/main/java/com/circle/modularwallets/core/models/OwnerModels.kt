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

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The owner identifier type for address mapping.
 */
enum class OwnerIdentifierType(val value: String) {
    EOA("EOAOWNER"),
    WebAuthn("WEBAUTHOWNER");
}

/**
 * The EOA identifier for address mapping.
 */
@JsonClass(generateAdapter = true)
open class EOAIdentifier(
    @Json(name = "address")
    open val address: String
)

/**
 * The WebAuthn identifier for address mapping.
 */
@JsonClass(generateAdapter = true)
open class WebAuthnIdentifier(
    @Json(name = "publicKeyX")
    open val publicKeyX: String,
    @Json(name = "publicKeyY")
    open val publicKeyY: String
)

/**
 * The base case of owner for address mapping.
 */
@JsonClass(generateAdapter = true)
open class AddressMappingOwner(
    /**
     * The owner identifier type for address mapping. See [OwnerIdentifierType].
     */
    @Json(name = "type") val type: String
)

/**
 * The EOA owner for address mapping.
 */
@JsonClass(generateAdapter = true)
data class EoaAddressMappingOwner(
    @Json(name = "identifier") val identifier: EOAIdentifier,
) : AddressMappingOwner(OwnerIdentifierType.EOA.value)

/**
 * The WebAuthn owner for address mapping.
 */
@JsonClass(generateAdapter = true)
data class WebAuthnAddressMappingOwner(
    @Json(name = "identifier") val identifier: WebAuthnIdentifier,
) : AddressMappingOwner(OwnerIdentifierType.WebAuthn.value)

/**
 * The response from adding an address mapping.
 */
@JsonClass(generateAdapter = true)
data class CreateAddressMappingResult(
    /**
     * The mapping ID.
     */
    @Json(name = "id") val id: String,
    /**
     * The blockchain identifier.
     */
    @Json(name = "blockchain") val blockchain: String,
    /**
     * The owner information.
     */
    @Json(name = "owner") val owner: AddressMappingOwner,
    /**
     * The wallet address.
     */
    @Json(name = "walletAddress") val walletAddress: String,
    /**
     * The creation date (ISO 8601 format).
     */
    @Json(name = "createDate") val createDate: String,
    /**
     * The last update date (ISO 8601 format).
     */
    @Json(name = "updateDate") val updateDate: String,
)