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

package com.circle.modularwallets.core.models

import com.circle.modularwallets.core.clients.PaymasterClient

/**
 * Sealed class for setting User Operation Paymaster configuration.
 *
 * If `paymaster` is `PaymasterClient`, it will use the provided Paymaster Client for sponsorship.
 * If `paymaster` is `true`, it will be assumed that the Bundler Client also supports Paymaster RPC methods
 * (e.g. `pm_getPaymasterData`), and use them for sponsorship.
 */
sealed class Paymaster {
    /**
     * Represents a Paymaster configuration where the Bundler Client supports Paymaster RPC methods.
     *
     * @property paymasterContext Optional context for the paymaster.
     */
    data class True @JvmOverloads constructor(
        val paymasterContext: Map<String, Any>? = null
    ) : Paymaster()

    /**
     * Represents a Paymaster configuration using a provided Paymaster Client for sponsorship.
     *
     * @property client The Paymaster Client used for sponsorship.
     * @property paymasterContext Optional context for the paymaster.
     */
    data class Client @JvmOverloads constructor(
        val client: PaymasterClient,
        val paymasterContext: Map<String, Any>? = null
    ) : Paymaster()
}