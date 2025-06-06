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

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigInteger

/**
 * The get user operation gas price response.
 */
@JsonClass(generateAdapter = true)
data class GetUserOperationGasPriceResult @JvmOverloads constructor(
    /**
     * The low gas price option.
     */
    @Json(name = "low") val low: GasPriceOption,

    /**
     * The medium gas price option.
     */
    @Json(name = "medium") val medium: GasPriceOption,

    /**
     * The high gas price option.
     */
    @Json(name = "high") val high: GasPriceOption,

    /**
     * The gas limit for deployed accounts.
     */
    @Json(name = "deployed") val deployed: BigInteger? = null,

    /**
     * The gas limit for not deployed accounts.
     */
    @Json(name = "notDeployed") val notDeployed: BigInteger? = null
)

/**
 * The gas price option.
 */
@JsonClass(generateAdapter = true)
data class GasPriceOption @JvmOverloads constructor(
    /**
     * The maximum fee per gas.
     */
    @Json(name = "maxFeePerGas") val maxFeePerGas: BigInteger,

    /**
     * The maximum priority fee per gas.
     */
    @Json(name = "maxPriorityFeePerGas") val maxPriorityFeePerGas: BigInteger
) 