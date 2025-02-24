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
import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.models.AuthenticationCredential
import com.circle.modularwallets.core.models.SignResult
import com.circle.modularwallets.core.models.toWebAuthnData
import com.circle.modularwallets.core.utils.fromJson
import com.circle.modularwallets.core.utils.signature.adjustSignature
import com.circle.modularwallets.core.utils.signature.hashMessage
import com.circle.modularwallets.core.utils.signature.hashTypedData
import com.circle.modularwallets.core.utils.signature.parseAsn1Signature
import com.circle.modularwallets.core.utils.signature.serializeSignature
import com.circle.modularwallets.core.utils.webauthn.getRequestOptions
import com.circle.modularwallets.core.utils.webauthn.getSavedCredentials

/**
 * Creates a WebAuthn account.
 *
 * @param credential The WebAuthn credential associated with the account.
 * @return The created WebAuthn account.
 */
fun toWebAuthnAccount(credential: WebAuthnCredential): WebAuthnAccount {
    return WebAuthnAccount(credential)
}

/**
 * Class representing a WebAuthn account.
 *
 * @param credential The WebAuthn credential associated with the account.
 */
open class WebAuthnAccount internal constructor(internal val credential: WebAuthnCredential) :
    Account<SignResult>() {
    /**
     * Retrieves the address of the WebAuthn account.
     *
     * @return The public key associated with the WebAuthn credential.
     */
    override fun getAddress(): String {
        return credential.publicKey
    }

    /**
     * Signs the given hex data.
     *
     * @param context The context used to launch framework UI flows ; use an activity context to make sure the UI will be launched within the same task stack.
     * @param hex The hex data to sign.
     * @return The result of the signing operation.
     * @throws BaseError if the credential request fails.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    override suspend fun sign(context: Context, hex: String): SignResult {
        val optionsAndJson = getRequestOptions(credential.rpId, credential.id, hex)
        val authRespJson = getSavedCredentials(context, optionsAndJson.second)
        val authResp = fromJson(authRespJson, AuthenticationCredential::class.java)
            ?: throw BaseError("credential request failed. Get null from json\n${authRespJson}")
        val ecdsaSigner = parseAsn1Signature(authResp.response.signature)
        val (r, s) = adjustSignature(ecdsaSigner.r, ecdsaSigner.s)
        val signatureHex = serializeSignature(r, s)
        return SignResult(
            signatureHex,
            authResp.toWebAuthnData(optionsAndJson.first.userVerification),
            authResp
        )
    }

    /**
     * Signs the given message.
     *
     * @param context The context used to launch framework UI flows ; use an activity context to make sure the UI will be launched within the same task stack.
     * @param message The message to sign.
     * @return The result of the signing operation.
     * @throws BaseError if the credential request fails.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    override suspend fun signMessage(context: Context, message: String): SignResult {
        val hash = hashMessage(message.toByteArray())
        return sign(context, hash)
    }

    /**
     * Signs the given typed data.
     *
     * @param context The context used to launch framework UI flows ; use an activity context to make sure the UI will be launched within the same task stack.
     * @param typedData The typed data to sign.
     * @return The result of the signing operation.
     * @throws BaseError if the credential request fails.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    override suspend fun signTypedData(context: Context, typedData: String): SignResult {
        val hash = hashTypedData(typedData)
        return sign(context, hash)
    }
}

