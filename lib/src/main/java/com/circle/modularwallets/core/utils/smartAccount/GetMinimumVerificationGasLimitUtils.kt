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


package com.circle.modularwallets.core.utils.smartAccount

import com.circle.modularwallets.core.chains.Mainnet
import com.circle.modularwallets.core.chains.Sepolia
import com.circle.modularwallets.core.constants.MAINNET_MINIMUM_UNDEPLOY_VERIFICATION_GAS_LIMIT
import com.circle.modularwallets.core.constants.MAINNET_MINIMUM_VERIFICATION_GAS_LIMIT
import com.circle.modularwallets.core.constants.MINIMUM_UNDEPLOY_VERIFICATION_GAS_LIMIT
import com.circle.modularwallets.core.constants.MINIMUM_VERIFICATION_GAS_LIMIT
import com.circle.modularwallets.core.constants.SEPOLIA_MINIMUM_UNDEPLOY_VERIFICATION_GAS_LIMIT
import com.circle.modularwallets.core.constants.SEPOLIA_MINIMUM_VERIFICATION_GAS_LIMIT
import com.circle.modularwallets.core.constants.gweiUnits
import java.math.BigInteger

/**
 * Gets the minimum verification gas limit for a given chain.
 *
 * @param deployed Whether the smart account is deployed.
 * @param chainId The chain ID.
 * @return The chain-specific minimum verification gas limit or the default value if the chain is not supported.
 */
@Throws(Exception::class)
fun getMinimumVerificationGasLimit(deployed: Boolean, chainId: Long): BigInteger {
    when(chainId){
        Sepolia.chainId -> {
            return if(deployed){
                SEPOLIA_MINIMUM_VERIFICATION_GAS_LIMIT
            } else{
                SEPOLIA_MINIMUM_UNDEPLOY_VERIFICATION_GAS_LIMIT
            }
        }
        Mainnet.chainId -> {
            return if(deployed){
                MAINNET_MINIMUM_VERIFICATION_GAS_LIMIT
            } else{
                MAINNET_MINIMUM_UNDEPLOY_VERIFICATION_GAS_LIMIT
            }
        }
        else -> {
            return if(deployed){
                MINIMUM_VERIFICATION_GAS_LIMIT
            } else{
                MINIMUM_UNDEPLOY_VERIFICATION_GAS_LIMIT
            }
        }
    }
}
