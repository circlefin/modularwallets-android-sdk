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
import com.circle.modularwallets.core.accounts.implementations.Web3jLocalAccount
import org.web3j.crypto.Credentials

/**
 * Creates a [LocalAccount] instance from a hexadecimal private key string.
 *
 * The underlying account implementation is [Web3jLocalAccount].
 *
 * @param privateKey The private key as a hexadecimal string.
 * @return A [LocalAccount] instance derived from the provided private key.
 */
fun privateKeyToAccount(privateKey: String): LocalAccount {
    val credentials = Credentials.create(privateKey)
    return LocalAccount(Web3jLocalAccount(credentials))
}

/**
 * Represents a local account with signing capabilities.
 *
 * Instances are typically created via factory functions like [mnemonicToAccount] or [privateKeyToAccount].
 *
 * @param delegate The underlying [Account] instance that this [LocalAccount] will delegate
 *                 its operations to. This delegate is responsible for the actual cryptographic
 *                 operations and key management.
 */
class LocalAccount(
    private val delegate: Account<String>
) : Account<String>() {

    /**
     * Retrieves the address of the local account.
     *
     * @return The address of the local account.
     */
    override fun getAddress(): String {
        return delegate.getAddress()
    }

    /**
     * Signs the given hex string.
     *
     * @param context The context in which the signing operation is performed.
     * @param hex The hex string to be signed.
     * @return The signed hex string.
     */
    override suspend fun sign(context: Context, hex: String): String {
        return delegate.sign(context, hex)
    }

    /**
     * Signs the given message.
     *
     * @param context The context in which the signing operation is performed.
     * @param message The message to be signed.
     * @return The signed message.
     */
    override suspend fun signMessage(context: Context, message: String): String {
        return delegate.signMessage(context, message)
    }

    /**
     * Signs the given typed data.
     *
     * @param context The context in which the signing operation is performed.
     * @param typedData The typed data to be signed.
     * @return The signed typed data.
     */
    override suspend fun signTypedData(context: Context, typedData: String): String {
        return delegate.signTypedData(context, typedData)
    }
}

