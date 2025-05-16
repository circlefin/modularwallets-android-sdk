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
import com.circle.modularwallets.core.apis.modular.ModularWallet
import com.circle.modularwallets.core.clients.Client
import com.circle.modularwallets.core.transports.Transport

interface CircleSmartAccountDelegate {

    suspend fun getModularWalletAddress(
        transport: Transport, version: String, name: String? = null
    ): ModularWallet

    suspend fun getComputeWallet(
        client: Client,
        version: String
    ): ModularWallet

    fun getFactoryData(): String

    suspend fun signAndWrap(
        context: Context,
        hash: String,
        hasUserOpGas: Boolean
    ): String
}