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

/**
 * Abstract class representing an account.
 *
 * @param T The type of the signed data.
 */
abstract class Account<T> {

    /**
     * Retrieves the address of the account.
     *
     * @return The address of the account.
     */
    abstract fun getAddress(): String

    /**
     * Signs the given hex data.
     *
     * @param context The context in which the signing operation is performed.
     * @param hex The hex data to sign.
     * @return The signed data of type T.
     */
    abstract suspend fun sign(context: Context, hex: String): T

    /**
     * Signs the given message.
     *
     * @param context The context in which the signing operation is performed.
     * @param message The message to sign.
     * @return The signed message of type T.
     */
    abstract suspend fun signMessage(context: Context, message: String): T

    /**
     * Signs the given typed data.
     *
     * @param context The context in which the signing operation is performed.
     * @param typedData The typed data to sign.
     * @return The signed typed data of type T.
     */
    abstract suspend fun signTypedData(context: Context, typedData: String): T
}