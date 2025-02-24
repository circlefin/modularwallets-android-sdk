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


import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.errors.BaseErrorParameters
import com.circle.modularwallets.core.utils.encoding.bytesToHex
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.DERSequenceGenerator
import org.bouncycastle.asn1.DLSequence
import org.web3j.crypto.ECDSASignature
import org.web3j.utils.Numeric.hexStringToByteArray
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger


internal val P256_N = BigInteger("FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", 16)
internal val P256_N_DIV_2 = P256_N.shiftRight(1)

internal fun adjustSignature(r: BigInteger, s: BigInteger): Pair<BigInteger, BigInteger> {
    return if (s > P256_N_DIV_2) {
        Pair(r, P256_N - s)
    } else {
        Pair(r, s)
    }
}

internal fun parseAsn1Signature(signature: String): ECDSASignature {
    val ecdsaSignature = fromDerFormat(base64UrlToBytes(signature))
    return ecdsaSignature
}

internal fun fromDerFormat(bytes: ByteArray?): ECDSASignature {
    try {
        ASN1InputStream(bytes).use { decoder ->
            val seq: DLSequence = decoder.readObject() as DLSequence
            val r: ASN1Integer
            val s: ASN1Integer
            try {
                r = seq.getObjectAt(0) as ASN1Integer
                s = seq.getObjectAt(1) as ASN1Integer
            } catch (e: ClassCastException) {
                throw BaseError("Invalid signature format", BaseErrorParameters(cause = e))
            }
            return ECDSASignature(r.positiveValue, s.positiveValue)
        }
    } catch (e: IOException) {
        throw BaseError("Invalid signature format", BaseErrorParameters(cause = e))
    }
}

internal fun parseSignature(hexSignature: String): String {
    val (r, s) = parseP256Signature(hexSignature)
    val esdsaSignature = ECDSASignature(r, s)
    val bytes = toDerFormat(esdsaSignature)
    return bytesToBase64Url(bytes).replace("\n", "")
}

internal fun toDerFormat(signature: ECDSASignature): ByteArray {
    try {
        ByteArrayOutputStream().use { baos ->
            val seq: DERSequenceGenerator = DERSequenceGenerator(baos)
            seq.addObject(ASN1Integer(signature.r))
            seq.addObject(ASN1Integer(signature.s))
            seq.close()
            return baos.toByteArray()
        }
    } catch (ex: IOException) {
        return ByteArray(0)
    }
}

internal fun serializeSignature(r: BigInteger, s: BigInteger): String {
    val result = ByteArray(64).apply {
        val rBytes = numberToBytesBE(r, 32)
        val sBytes = numberToBytesBE(s, 32)
        System.arraycopy(rBytes, 0, this, 0, rBytes.size)
        System.arraycopy(sBytes, 0, this, rBytes.size, sBytes.size)
    }
    return bytesToHex(result)
}

internal fun numberToBytesBE(n: BigInteger, len: Int): ByteArray {
    val hex = n.toString(16).padStart(len * 2, '0')
    return hexStringToByteArray(hex)
}
