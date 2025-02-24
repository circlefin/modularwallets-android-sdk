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


package com.circle.modularwallets.core.utils.webauthn

import android.content.Context
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialException
import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.errors.BaseErrorParameters

@ExcludeFromGeneratedCCReport
internal suspend fun createPasskey(
    context: Context,
    registrationJson: String
): CreatePublicKeyCredentialResponse {
    try {
        val request = CreatePublicKeyCredentialRequest(registrationJson)
        val credentialManager = CredentialManager.create(context)
        val response: CreatePublicKeyCredentialResponse = credentialManager.createCredential(
            context,
            request
        ) as CreatePublicKeyCredentialResponse

        PublicKeyCredential(response.registrationResponseJson)
        return response
    } catch (e: CreateCredentialException) {
        e.message?.let{
            if(it.contains("User cancelled the selector")) {
                throw BaseError(it, BaseErrorParameters(e))
            }
        }
        throw BaseError("credential request failed.", BaseErrorParameters(e, registrationJson))
    }
}

@ExcludeFromGeneratedCCReport
internal suspend fun getSavedCredentials(context: Context, authJson: String): String {
    val getPublicKeyCredentialOption =
        GetPublicKeyCredentialOption(authJson, null)
    val credentialManager = CredentialManager.create(context)
    val result = try {
        credentialManager.getCredential(
            context,
            GetCredentialRequest(
                listOf(
                    getPublicKeyCredentialOption,
                    GetPasswordOption()
                )
            )
        )
    } catch (e: Exception) {
        e.message?.let{
            if(it.contains("User cancelled the selector")) {
                throw BaseError(it, BaseErrorParameters(e))
            }
        }
        throw BaseError("credential request failed.", BaseErrorParameters(e))
    }

    if (result.credential is PublicKeyCredential) {
        val cred = result.credential as PublicKeyCredential
        return cred.authenticationResponseJson
    }
    throw BaseError("credential request failed. No PublicKeyCredential")
}