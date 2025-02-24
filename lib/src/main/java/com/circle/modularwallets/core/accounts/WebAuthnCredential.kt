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


package com.circle.modularwallets.core.accounts

import android.content.Context
import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.apis.rp.RpApiImpl
import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.models.AuthenticationCredential
import com.circle.modularwallets.core.models.PublicKeyCredential
import com.circle.modularwallets.core.models.RegistrationCredential
import com.circle.modularwallets.core.models.WebAuthnMode
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.fromJson
import com.circle.modularwallets.core.utils.signature.parseCredentialPublicKey
import com.circle.modularwallets.core.utils.signature.serializePublicKey
import com.circle.modularwallets.core.utils.webauthn.createPasskey
import com.circle.modularwallets.core.utils.webauthn.getSavedCredentials

/**
 * Logs in or registers a user and returns a WebAuthnCredential.
 *
 * @param context The context used to launch framework UI flows ; use an activity context to make sure the UI will be launched within the same task stack.
 * @param transport The transport used to communicate with the RP API.
 * @param userName  The username of the user. (required for WebAuthnMode.Register)
 * @param mode The mode of the WebAuthn credential.
 * @return The created WebAuthn credential.
 * Throws: BaseError if userName is null for WebAuthnMode.Register.
 *
 */

@ExcludeFromGeneratedCCReport
@Throws(Exception::class)
@JvmOverloads
suspend fun toWebAuthnCredential(
    context: Context,
    transport: Transport,
    userName: String? = null,
    mode: WebAuthnMode
): WebAuthnCredential {
    return when (mode) {
        WebAuthnMode.Register -> {
            userName ?: throw BaseError("userName cannot be null")
            WebAuthnCredential.register(context, transport, userName)
        }

        WebAuthnMode.Login -> WebAuthnCredential.login(context, transport)
    }
}

/**
 * Data class representing a P-256 WebAuthn Credential.
 *
 * @param id The unique identifier for the credential.
 * @param publicKey The public key associated with the credential.
 * @param raw Web Authentication API returned PublicKeyCredential object.
 * @param rpId The relying party identifier.
 */
class WebAuthnCredential(
    val id: String,
    val publicKey: String,
    val raw: PublicKeyCredential,
    val rpId: String
) {
    companion object {
        @ExcludeFromGeneratedCCReport
        internal suspend fun register(
            context: Context,
            transport: Transport,
            userName: String
        ): WebAuthnCredential {
            /** 1. RP getRegistrationOptions */
            val rpApi =
                RpApiImpl(transport)
            val (options, optionsJson) = rpApi.getRegistrationOptions(userName)
            /** 2. Create credential */
            val registerRespJson = createPasskey(context, optionsJson).registrationResponseJson
            val registerResp = fromJson(registerRespJson, RegistrationCredential::class.java)
                ?: throw BaseError("credential request failed. RegistrationCredential is null\n${registerRespJson}")
            /** 3. RP getRegistrationVerification */
            rpApi.getRegistrationVerification(registerResp)
            /** 4. Parse and serialized public key */
            val publicKey = parseCredentialPublicKey(registerResp.response.publicKey)
            val serializedPublicKey = serializePublicKey(publicKey)
            return WebAuthnCredential(
                registerResp.id,
                serializedPublicKey,
                registerResp,
                options.rp.id
            )
        }

        @ExcludeFromGeneratedCCReport
        internal suspend fun login(
            context: Context,
            transport: Transport
        ): WebAuthnCredential {
            /** 1. RP getLoginOptions */
            val rpApi =
                RpApiImpl(transport)
            val (options, optionsJson) = rpApi.getLoginOptions()

            /** 2. Get credential */
            val authRespJson = getSavedCredentials(context, optionsJson)
            val authResp = fromJson(authRespJson, AuthenticationCredential::class.java)
                ?: throw BaseError("credential request failed. AuthenticationCredential is null\n${authRespJson}")

            /** 3. RP getLoginVerification */
            val cPublicKey = rpApi.getLoginVerification(authResp)

            /** 4. Parse and serialized public key */
            val publicKey = parseCredentialPublicKey(cPublicKey)
            val serializedPublicKey = serializePublicKey(publicKey)
            return WebAuthnCredential(authResp.id, serializedPublicKey, authResp, options.rpId)
        }
    }
}