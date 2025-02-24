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

import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.models.WebAuthnData
import com.circle.modularwallets.core.utils.encoding.bytesToHex
import org.web3j.utils.Numeric.hexStringToByteArray
import java.security.MessageDigest
import java.security.PublicKey
import java.security.Signature

@ExcludeFromGeneratedCCReport
internal fun verifyBasic(
    hash: String,
    authenticatorDataBytes: ByteArray,
    webAuthn: WebAuthnData
): Boolean {
    // Check length of `authenticatorData`.
    if (authenticatorDataBytes.size < 37) return false

    val flag = authenticatorDataBytes[32].toInt()

    // Verify that the UP bit of the flags in authData is set.
    if ((flag and 0x01) != 0x01) return false

    // If user verification was determined to be required, verify that
    // the UV bit of the flags in authData is set. Otherwise, ignore the
    // value of the UV flag.
    if (webAuthn.userVerificationRequired && (flag and 0x04) != 0x04) return false

    // If the BE bit of the flags in authData is not set, verify that
    // the BS bit is not set.
    if ((flag and 0x08) != 0x08 && (flag and 0x10) == 0x10) return false

    // Check that response is for an authentication assertion
    val expectedType = "\"type\":\"webauthn.get\""
    val typeSlice = webAuthn.clientDataJSON.substring(
        webAuthn.typeIndex,
        webAuthn.typeIndex + expectedType.length
    )
    if (typeSlice != expectedType) return false

    // Check that hash is in the clientDataJSON.
    val challenge =
        extractChallenge(webAuthn.clientDataJSON, webAuthn.challengeIndex) ?: return false
    // Validate the challenge in the clientDataJSON.
    return bytesToHex(base64UrlToBytes(challenge)) == hash
}

@ExcludeFromGeneratedCCReport
internal fun verifyRaw(
    publicKey: PublicKey,
    signature: String,
    clientDataJSON: String,
    authenticatorData: String
): Boolean {
    val signatureBytes = base64UrlToBytes(signature)
    val sig = Signature.getInstance("SHA256withECDSA")
    sig.initVerify(publicKey)
    val md = MessageDigest.getInstance("SHA-256")
    val clientDataHash = md.digest(base64UrlToBytes(clientDataJSON))
    val signatureBase = base64UrlToBytes(authenticatorData) + clientDataHash
    sig.update(signatureBase)
    return sig.verify(signatureBytes)
}

@ExcludeFromGeneratedCCReport
internal fun verify(
    hash: String,
    publicKey: String,
    hexSignature: String,
    webauthn: WebAuthnData
): Boolean {
    val ecPublicKey = parsePublicKey(publicKey)
    val signature = parseSignature(hexSignature)
    val clientDataJSON =
        bytesToBase64Url(webauthn.clientDataJSON.toByteArray()).replace("\n", "")
    val authenticatorDataBytes = hexStringToByteArray(webauthn.authenticatorData)
    val authenticatorData =
        bytesToBase64Url(authenticatorDataBytes).replace("\n", "")
    if (!verifyBasic(hash, authenticatorDataBytes, webauthn)) {
        return false
    }
    return verifyRaw(ecPublicKey, signature, clientDataJSON, authenticatorData)
}

@ExcludeFromGeneratedCCReport
private fun extractChallenge(clientDataJSON: String, challengeIndex: Int): String? {
    val regex = """"challenge":"([^"]+)"""".toRegex()
    val challengeSubstring = clientDataJSON.substring(challengeIndex)
    val matchResult = regex.find(challengeSubstring)
    return matchResult?.groups?.get(1)?.value
}
