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
import com.circle.modularwallets.core.utils.encoding.bytesToHex
import com.circle.modularwallets.core.utils.encoding.hexToBigInteger
import org.web3j.utils.Numeric.hexStringToByteArray
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECFieldFp
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec
import java.security.spec.EllipticCurve
import java.security.spec.X509EncodedKeySpec

internal fun parseCredentialPublicKey(cPublicKey: String): ECPublicKey {
    val bytes = base64UrlToBytes(cPublicKey)
    val publicKey = toECPublicKey(bytes)
    return publicKey
}

internal fun toECPublicKey(bytes: ByteArray): ECPublicKey {
    val keySpec = X509EncodedKeySpec(bytes)
    val keyFactory = KeyFactory.getInstance("EC")
    val publicKey = keyFactory.generatePublic(keySpec)
    if (publicKey !is ECPublicKey) {
        throw BaseError("PublicKey is not of type ECPublicKey")
    }
    return publicKey
}

internal fun parsePublicKey(hexString: String): ECPublicKey {
    val (x, y) = parseP256Signature(hexString)
    /**
     * References for p, a, b, gx, gy, n:
     * https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.186-4.pdf, p.101
     * https://www.secg.org/sec2-v2.pdf, p.14
     * */
    val p = BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")
    val a = BigInteger("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", 16)
    val b = BigInteger("5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b", 16)
    val gx = BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296", 16)
    val gy = BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5", 16)
    val n = BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369")

    val curve = EllipticCurve(ECFieldFp(p), a, b)
    val g = ECPoint(gx, gy)
    val ecParameterSpec = ECParameterSpec(curve, g, n, 1)

    val ecPoint = ECPoint(x, y)
    val keySpec = ECPublicKeySpec(ecPoint, ecParameterSpec)

    val keyFactory = KeyFactory.getInstance("EC")
    return keyFactory.generatePublic(keySpec) as ECPublicKey
}

internal fun parseP256Signature(hex: String): Pair<BigInteger, BigInteger> {
    val xy = parseP256SignatureToBytes(hex)
    return Pair(hexToBigInteger(bytesToHex(xy.first))!!, hexToBigInteger(bytesToHex(xy.second))!!)
}

internal fun parseP256SignatureToBytes(hex: String): Pair<ByteArray, ByteArray> {
    // xy for public key, rs for signature
    val bytes = hexStringToByteArray(hex)
    val offset = if (bytes.size == 65) 1 else 0

    val x = bytes.copyOfRange(offset, 32 + offset)
    val y = bytes.copyOfRange(32 + offset, 64 + offset)

    return Pair(x, y)
}

internal fun serializePublicKey(publicKey: ECPublicKey): String {
    val point = publicKey.w
    val x = hexToBigInteger(bytesToHex(point.affineX.toByteArray())) ?: throw BaseError("x is null")
    val y = hexToBigInteger(bytesToHex(point.affineY.toByteArray())) ?: throw BaseError("y is null")
    return serializePublicKey(x, y)
}

internal fun serializePublicKey(x: BigInteger, y: BigInteger): String {
    val result = mutableListOf<Byte>()
    result.addAll(numberToBytesBE(x, 32).toList())
    result.addAll(numberToBytesBE(y, 32).toList())
    return bytesToHex(result.toByteArray())
}
