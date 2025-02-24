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

import android.util.Base64
import com.circle.modularwallets.core.apis.rp.PublicKeyCredentialDescriptor
import com.circle.modularwallets.core.apis.rp.PublicKeyCredentialRequestOptions
import com.circle.modularwallets.core.utils.signature.bytesToBase64Url
import com.circle.modularwallets.core.utils.toJson
import org.web3j.utils.Numeric.hexStringToByteArray
import java.security.SecureRandom

internal fun getRequestOptions(
    rpId: String,
    allowCredentialId: String? = null,
    hex: String
): Pair<PublicKeyCredentialRequestOptions, String> {
    val challenge: String = bytesToBase64Url(hexStringToByteArray(hex)) //getEncodedChallenge()
    val allowCredentials = if (allowCredentialId?.isNotBlank() == true) mutableListOf(
        PublicKeyCredentialDescriptor(allowCredentialId)
    ) else null
    val options = PublicKeyCredentialRequestOptions(challenge, rpId, allowCredentials)
    return Pair(options, toJson(options))
}

private fun getEncodedUserId(): String {
    val random = SecureRandom()
    val bytes = ByteArray(64)
    random.nextBytes(bytes)
    return Base64.encodeToString(
        bytes,
        Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
    )
}

internal fun getEncodedChallenge(): String {
    val random = SecureRandom()
    val bytes = ByteArray(32)
    random.nextBytes(bytes)
    return Base64.encodeToString(
        bytes,
        Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
    )
}
