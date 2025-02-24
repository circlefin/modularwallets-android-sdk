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

package com.circle.modularwallets.core.apis.util

import com.circle.modularwallets.core.constants.CIRCLE_WEIGHTED_WEB_AUTHN_MULTISIG_PLUGIN
import com.circle.modularwallets.core.transports.Transport
import java.math.BigInteger

internal interface UtilApi {
    suspend fun getAddress(
        transport: Transport,
        to: String,
        sender: ByteArray,
        salt: ByteArray,
        initializingData: ByteArray,
    ): Pair<String, String>

    suspend fun createAccount(
        transport: Transport,
        to: String,
        sender: String,
        salt: ByteArray,
        initializingData: ByteArray,
    ): String

    suspend fun getNonce(
        transport: Transport,
        address: String,
        to: String,
        key: BigInteger = BigInteger.ZERO
    ): BigInteger

    suspend fun getMaxPriorityFeePerGas(
        transport: Transport,
    ): BigInteger

    suspend fun isValidSignature(
        transport: Transport,
        message: String,
        signature: String,
        from: String,
        to: String = CIRCLE_WEIGHTED_WEB_AUTHN_MULTISIG_PLUGIN.address
    ): Boolean

}