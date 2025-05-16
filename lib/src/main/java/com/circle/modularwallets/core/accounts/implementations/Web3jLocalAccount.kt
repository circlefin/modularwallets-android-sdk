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

package com.circle.modularwallets.core.accounts.implementations

import android.content.Context
import com.circle.modularwallets.core.accounts.Account
import com.circle.modularwallets.core.utils.encoding.toBytes
import com.circle.modularwallets.core.utils.signature.hashTypedData
import com.circle.modularwallets.core.utils.signature.serializeSignature
import org.web3j.crypto.Credentials
import org.web3j.crypto.Sign

/**
 * An internal implementation of [Account] that uses Web3j [Credentials]
 * for cryptographic operations such as signing messages and transactions.
 * This class directly handles interactions with the Web3j library for local accounts.
 *
 * @param credentials The Web3j [Credentials] associated with this local account,
 * containing the key pair used for signing.
 */
internal class Web3jLocalAccount(private val credentials: Credentials) : Account<String>() {

    /**
     * Retrieves the Ethereum address associated with the [credentials].
     *
     * @return The Ethereum address for the current local account.
     */
    override fun getAddress(): String {
        return credentials.address
    }

    /**
     * Signs the given hexadecimal string, typically representing a pre-hashed message or transaction hash.
     * This method uses `Sign.signMessage` from Web3j, which does not apply the Ethereum message prefix.
     *
     * @param context The Android [Context]. Currently not used in this specific signing operation
     *                but available for potential future extensions (e.g., hardware-backed signing).
     * @param hex The hexadecimal string (e.g., a 32-byte hash) to be signed.
     * @return The ECDSA signature as a serialized hex string (r + s + v).
     */
    override suspend fun sign(context: Context, hex: String): String {
        val signatureData = Sign.signMessage(toBytes(hex), credentials.ecKeyPair, false)
        return serializeSignature(signatureData)
    }

    /**
     * Signs the given message string after applying the standard Ethereum message prefix
     * (`\x19Ethereum Signed Message:\n` + message length).
     * This method uses `Sign.signPrefixedMessage` from Web3j.
     *
     * @param context The Android [Context]. Currently not used in this specific signing operation
     *                but available for potential future extensions.
     * @param message The plain string message to be signed. It will be UTF-8 encoded.
     * @return The ECDSA signature as a serialized hex string (r + s + v).
     */
    override suspend fun signMessage(context: Context, message: String): String {
        val signatureData = Sign.signPrefixedMessage(toBytes(message), credentials.ecKeyPair)
        return serializeSignature(signatureData)
    }

    /**
     * Signs the given EIP-712 typed data.
     * The input [typedData] string is expected to be a JSON representation compliant with EIP-712.
     * This method first computes the EIP-712 hash of the typed data and then signs this hash
     * using the [sign] method (which does not add an additional Ethereum prefix).
     *
     * @param context The Android [Context]. Currently not used in this specific signing operation
     *                but available for potential future extensions.
     * @param typedData The EIP-712 typed data as a JSON string.
     * @return The ECDSA signature as a serialized hex string (r + s + v).
     */
    override suspend fun signTypedData(context: Context, typedData: String): String {
        val hash = hashTypedData(typedData)
        return sign(context, hash)
    }
}

