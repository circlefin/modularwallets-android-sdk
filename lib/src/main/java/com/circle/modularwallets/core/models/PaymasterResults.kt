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
 * Data class representing the result of getting paymaster data.
 *
 * @property paymasterAndData Combined paymaster and data.
 * @property paymasterData Paymaster data.
 * @property paymaster Paymaster address.
 * @property paymasterPostOpGasLimit Gas limit for post-operation of paymaster.
 * @property paymasterVerificationGasLimit Gas limit for verification of paymaster.
 */
@JsonClass(generateAdapter = true)
data class GetPaymasterDataResult @JvmOverloads constructor(
    @Json(name = "paymasterAndData") var paymasterAndData: String? = null,
    @Json(name = "paymasterData") var paymasterData: String? = null,
    @Json(name = "paymaster") var paymaster: String? = null,
    @Json(name = "paymasterPostOpGasLimit") var paymasterPostOpGasLimit: BigInteger? = null,
    @Json(name = "paymasterVerificationGasLimit") var paymasterVerificationGasLimit: BigInteger? = null,
)

/**
 * Data class representing the result of getting paymaster stub data.
 *
 * @property paymasterAndData Combined paymaster and data.
 * @property paymaster Paymaster address.
 * @property paymasterData Paymaster data.
 * @property paymasterPostOpGasLimit Gas limit for post-operation of paymaster.
 * @property paymasterVerificationGasLimit Gas limit for verification of paymaster.
 * @property isFinal Indicates if the data is final.
 * @property sponsor Sponsor information.
 */
@JsonClass(generateAdapter = true)
data class GetPaymasterStubDataResult @JvmOverloads constructor(
    @Json(name = "paymasterAndData") var paymasterAndData: String? = null,
    @Json(name = "paymaster") var paymaster: String? = null,
    @Json(name = "paymasterData") var paymasterData: String? = null,
    @Json(name = "paymasterPostOpGasLimit") var paymasterPostOpGasLimit: BigInteger? = null,
    @Json(name = "paymasterVerificationGasLimit") var paymasterVerificationGasLimit: BigInteger? = null,
    @Json(name = "isFinal") var isFinal: Boolean? = null,
    @Json(name = "sponsor") var sponsor: Sponsor? = null,
)
/**
 * Data class representing sponsor information.
 *
 * @property name Sponsor name.
 * @property icon Sponsor icon.
 */
@JsonClass(generateAdapter = true)
data class Sponsor @JvmOverloads constructor(
    var name: String? = null,
    var icon: String? = null
)