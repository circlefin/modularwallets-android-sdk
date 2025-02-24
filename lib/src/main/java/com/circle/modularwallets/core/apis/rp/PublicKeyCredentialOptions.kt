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


package com.circle.modularwallets.core.apis.rp

import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// PublicKeyCredentialCreationOptions
@ExcludeFromGeneratedCCReport
@JsonClass(generateAdapter = true)
internal data class PublicKeyCredentialCreationOptions(
    @Json(name = "challenge") val challenge: String,
    @Json(name = "rp") val rp: PublicKeyCredentialRpEntity,
    @Json(name = "pubKeyCredParams") val pubKeyCredParams: List<PublicKeyCredentialParameters> = listOf(
        PublicKeyCredentialParameters(-7, "public-key"),
        PublicKeyCredentialParameters(-257, "public-key"),
    ),
    @Json(name = "authenticatorSelection") val authenticatorSelection: AuthenticatorSelectionCriteria = AuthenticatorSelectionCriteria(
        residentKey = "required"
    ),
    @Json(name = "user") var user: PublicKeyCredentialUserEntity,
    @Json(name = "timeout") val timeout: Long? = null,
    @Json(name = "excludeCredentials") val excludeCredentials: MutableList<PublicKeyCredentialDescriptor>? = null,
    @Json(name = "attestation") val attestation: String? = null,
    @Json(name = "extensions") val extensions: String? = null,
)
@ExcludeFromGeneratedCCReport
@JsonClass(generateAdapter = true)
internal data class AuthenticatorSelectionCriteria(
    @Json(name = "authenticatorAttachment") val authenticatorAttachment: String? = null,
    @Json(name = "residentKey") val residentKey: String? = null,
    @Json(name = "requireResidentKey") val requireResidentKey: Boolean? = null,
    @Json(name = "userVerification") val userVerification: String? = null,
)
@ExcludeFromGeneratedCCReport
@JsonClass(generateAdapter = true)
internal data class PublicKeyCredentialRpEntity(
    @Json(name = "id") var id: String,
    @Json(name = "name") val name: String = "",
)
@ExcludeFromGeneratedCCReport
@JsonClass(generateAdapter = true)
internal data class PublicKeyCredentialParameters(
    @Json(name = "alg") val alg: Int,
    @Json(name = "type") val type: String,
)
@ExcludeFromGeneratedCCReport
@JsonClass(generateAdapter = true)
internal data class PublicKeyCredentialUserEntity(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "displayName") val displayName: String,
)

// PublicKeyCredentialRequestOptions
@ExcludeFromGeneratedCCReport
@JsonClass(generateAdapter = true)
internal data class PublicKeyCredentialRequestOptions(
    @Json(name = "challenge") val challenge: String,
    @Json(name = "rpId") var rpId: String,
    @Json(name = "allowCredentials") var allowCredentials: MutableList<PublicKeyCredentialDescriptor>? = null,
    @Json(name = "timeout") val timeout: Long = 1800000,
    @Json(name = "userVerification") val userVerification: String = "required",
)
@ExcludeFromGeneratedCCReport
@JsonClass(generateAdapter = true)
internal data class PublicKeyCredentialDescriptor(
    @Json(name = "id") val id: String,
    @Json(name = "transports") val transports: List<String>? = null,
    @Json(name = "type") val type: String = "public-key",
)