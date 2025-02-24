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


package com.circle.modularwallets.core.apis.modular

import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents the Circle modular wallet.
 *
 * @property address The wallet address.
 * @property blockchain The blockchain.
 * @property state The state.
 * @property name The name of the wallet.
 * @property scaConfiguration The SCA configuratio.
 */
@JsonClass(generateAdapter = true)
data class ModularWallet(
    @Json(name = "address") val address: String,
    @Json(name = "blockchain") val blockchain: String? = null,
    @Json(name = "state") val state: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "scaConfiguration") val scaConfiguration: ScaConfiguration,
){
    /**
     * Gets the initialization code from the SCA configuration.
     *
     * @return The initialization code if present, null otherwise.
     */
    fun getInitCode(): String? {
        return scaConfiguration.initCode
    }
}

@JsonClass(generateAdapter = true)
internal data class GetAddressReq(
    @Json(name = "scaConfiguration") val scaConfiguration: ScaConfiguration,
    @Json(name = "metadata") val matadata: Metadata,
)

@JsonClass(generateAdapter = true)
data class ScaConfiguration(
    @Json(name = "initialOwnershipConfiguration") val initialOwnershipConfiguration: InitialOwnershipConfiguration? = null,
    @Json(name = "scaCore") val scaCore: String?, // req
    @Json(name = "initCode") val initCode: String? = null, // resp
)

@JsonClass(generateAdapter = true)
data class Metadata(
    @Json(name = "name") val name: String? = null,
)

@JsonClass(generateAdapter = true)
data class InitialOwnershipConfiguration(
    @Json(name = "weightedMultisig") val weightedMultisig: WeightedMultiSig,
    @Json(name = "ownershipContractAddress") val ownershipContractAddress: String? = null, // resp
)

@JsonClass(generateAdapter = true)
data class WeightedMultiSig(
    @Json(name = "webauthnOwners") val webauthnOwners: Array<WebauthnOwner>,
    @Json(name = "thresholdWeight") val thresholdWeight: Int,
    @Json(name = "owners") val owners: Array<EoaOwner>? = null,
)

@ExcludeFromGeneratedCCReport
@JsonClass(generateAdapter = true)
data class EoaOwner(
    @Json(name = "address") val address: String,
    @Json(name = "weight") val weight: Int,
)

@JsonClass(generateAdapter = true)
data class WebauthnOwner(
    @Json(name = "publicKeyX") val publicKeyX: String,
    @Json(name = "publicKeyY") val publicKeyY: String,
    @Json(name = "weight") val weight: Int,
)


internal fun getCreateWalletReq(
    publicKeyX: String,
    publicKeyY: String,
    version: String,
    name: String? = null
): GetAddressReq {
    return GetAddressReq(
        ScaConfiguration(
            InitialOwnershipConfiguration(
                WeightedMultiSig(
                    arrayOf(
                        WebauthnOwner(
                            publicKeyX, publicKeyY, 1
                        )
                    ), 1
                )
            ),
            version
        ),
        Metadata(name)
    )
}