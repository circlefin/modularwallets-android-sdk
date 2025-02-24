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

package com.circle.modularwallets.core.apis.paymaster

import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.models.GetPaymasterDataResult
import com.circle.modularwallets.core.models.GetPaymasterStubDataResult
import com.circle.modularwallets.core.models.Sponsor
import com.circle.modularwallets.core.utils.encoding.hexToBigInteger
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigInteger

@ExcludeFromGeneratedCCReport
@JsonClass(generateAdapter = true)
internal data class GetPaymasterDataResp(
    @Json(name = "paymasterAndData") var paymasterAndData: String? = null,
    @Json(name = "paymasterData") var paymasterData: String? = null,
    @Json(name = "paymaster") var paymaster: String? = null,
    @Json(name = "paymasterPostOpGasLimit") var paymasterPostOpGasLimitHex: String? = null,
    @Json(name = "paymasterVerificationGasLimit") var paymasterVerificationGasLimitHex: String? = null,
) {
    val paymasterPostOpGasLimit: BigInteger?
        get() = hexToBigInteger(paymasterPostOpGasLimitHex)
    val paymasterVerificationGasLimit: BigInteger?
        get() = hexToBigInteger(paymasterVerificationGasLimitHex)
}

internal fun GetPaymasterDataResp.toResult(): GetPaymasterDataResult {
    return GetPaymasterDataResult(
        paymasterAndData = this.paymasterAndData,
        paymaster = this.paymaster,
        paymasterData = this.paymasterData,
        paymasterPostOpGasLimit = this.paymasterPostOpGasLimit,
        paymasterVerificationGasLimit = this.paymasterVerificationGasLimit,
    )
}

@ExcludeFromGeneratedCCReport
@JsonClass(generateAdapter = true)
internal class GetPaymasterStubDataResp(
    @Json(name = "paymasterAndData") var paymasterAndData: String? = null,
    @Json(name = "paymaster") var paymaster: String? = null,
    @Json(name = "paymasterData") var paymasterData: String? = null,
    @Json(name = "paymasterPostOpGasLimit") var paymasterPostOpGasLimitHex: String? = null,
    @Json(name = "paymasterVerificationGasLimit") var paymasterVerificationGasLimitHex: String? = null,
    @Json(name = "isFinal") var isFinal: Boolean = false,
    @Json(name = "sponsor") var sponsor: Sponsor? = null,
) {
    val paymasterPostOpGasLimit: BigInteger?
        get() = hexToBigInteger(paymasterPostOpGasLimitHex)
    val paymasterVerificationGasLimit: BigInteger?
        get() = hexToBigInteger(paymasterVerificationGasLimitHex)
}

internal fun GetPaymasterStubDataResp.toResult(): GetPaymasterStubDataResult {
    return GetPaymasterStubDataResult(
        paymasterAndData = this.paymasterAndData,
        paymaster = this.paymaster,
        paymasterData = this.paymasterData,
        paymasterPostOpGasLimit = this.paymasterPostOpGasLimit,
        paymasterVerificationGasLimit = this.paymasterVerificationGasLimit,
        isFinal = this.isFinal,
        sponsor = this.sponsor,
    )
}