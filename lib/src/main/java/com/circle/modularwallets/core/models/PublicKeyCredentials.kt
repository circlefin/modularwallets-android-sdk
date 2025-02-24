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
 * Abstract class representing a public key credential.
 *
 * @property id The unique identifier for the credential.
 * @property type The type of the credential.
 * @property authenticatorAttachment The attachment type of the authenticator.
 * @property response The response from the authenticator.
 * @property clientExtensionResults Optional client extension results.
 */
abstract class PublicKeyCredential {
    abstract val id: String
    abstract val type: String
    abstract val authenticatorAttachment: String
    abstract val response: AuthenticatorResponse
    abstract val clientExtensionResults: AuthenticationExtensionsClientOutputs?
}

/**
 * Abstract class representing a response from an authenticator.
 *
 * @property clientDataJSON The client data in JSON format.
 */
abstract class AuthenticatorResponse {
    abstract val clientDataJSON: String
}

/**
 * Data class representing a registration credential.
 *
 * @property rawId The raw identifier for the credential.
 * @property authenticatorAttachment The attachment type of the authenticator.
 * @property type The type of the credential.
 * @property id The unique identifier for the credential.
 * @property response The attestation response from the authenticator.
 * @property clientExtensionResults Optional client extension results.
 */
@JsonClass(generateAdapter = true)
data class RegistrationCredential(
    @Json(name = "rawId") val rawId: String,
    @Json(name = "authenticatorAttachment") override val authenticatorAttachment: String,
    @Json(name = "type") override val type: String,
    @Json(name = "id") override val id: String,
    @Json(name = "response") override val response: AuthenticatorAttestationResponse,
    @Json(name = "clientExtensionResults") override val clientExtensionResults: AuthenticationExtensionsClientOutputs?,
) : PublicKeyCredential()

/**
 * Data class representing an attestation response from an authenticator.
 *
 * @property clientDataJSON The client data in JSON format.
 * @property attestationObject The attestation object.
 * @property transports The list of supported transports.
 * @property authenticatorData The authenticator data.
 * @property publicKeyAlgorithm The public key algorithm.
 * @property publicKey The public key.
 */
@JsonClass(generateAdapter = true)
data class AuthenticatorAttestationResponse(
    @Json(name = "clientDataJSON") override val clientDataJSON: String,
    @Json(name = "attestationObject") val attestationObject: String,
    @Json(name = "transports") val transports: List<String>,
    @Json(name = "authenticatorData") val authenticatorData: String,
    @Json(name = "publicKeyAlgorithm") val publicKeyAlgorithm: Int,
    @Json(name = "publicKey") val publicKey: String,
) : AuthenticatorResponse()

/**
 * Data class representing an authentication credential.
 *
 * @property rawId The raw identifier for the credential.
 * @property authenticatorAttachment The attachment type of the authenticator.
 * @property type The type of the credential.
 * @property id The unique identifier for the credential.
 * @property response The assertion response from the authenticator.
 * @property clientExtensionResults Optional client extension results.
 */
@JsonClass(generateAdapter = true)
data class AuthenticationCredential(
    @Json(name = "rawId") val rawId: String,
    @Json(name = "authenticatorAttachment") override val authenticatorAttachment: String,
    @Json(name = "type") override val type: String,
    @Json(name = "id") override val id: String,
    @Json(name = "response") override val response: AuthenticatorAssertionResponse,
    @Json(name = "clientExtensionResults") override val clientExtensionResults: AuthenticationExtensionsClientOutputs?,
) : PublicKeyCredential()

/**
 * Data class representing an assertion response from an authenticator.
 *
 * @property clientDataJSON The client data in JSON format.
 * @property authenticatorData The authenticator data.
 * @property signature The signature.
 * @property userHandle The user handle.
 */
@JsonClass(generateAdapter = true)
data class AuthenticatorAssertionResponse(
    @Json(name = "clientDataJSON") override val clientDataJSON: String,
    @Json(name = "authenticatorData") val authenticatorData: String,
    @Json(name = "signature") val signature: String,
    @Json(name = "userHandle") val userHandle: String,
) : AuthenticatorResponse()

/**
 * Data class representing client extension outputs for authentication.
 *
 * @property credProps Optional credential properties output.
 */
@JsonClass(generateAdapter = true)
data class AuthenticationExtensionsClientOutputs @JvmOverloads constructor(
    @Json(name = "credProps") val credProps: CredentialPropertiesOutput? = null,
)

/**
 * Data class representing credential properties output.
 *
 * @property rk Optional resident key property.
 */
@JsonClass(generateAdapter = true)
data class CredentialPropertiesOutput(
    @Json(name = "rk") val rk: Boolean? = null,
)
