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

import com.circle.modularwallets.core.apis.modular.ModularApiImpl
import com.circle.modularwallets.core.constants.MINIMUM_UNDEPLOY_VERIFICATION_GAS_LIMIT
import com.circle.modularwallets.core.constants.MINIMUM_VERIFICATION_GAS_LIMIT
import java.math.BigInteger
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.models.GetUserOperationGasPriceResult

/**
 * Gets the minimum verification gas limit for a given chain.
 *
 * @param deployed Whether the smart account is deployed.
 * @param transport The transport to use for RPC calls.
 * @return The minimum verification gas limit, using backend value if available, otherwise fallback to hardcoded value.
 */
suspend fun getDefaultVerificationGasLimit(
    deployed: Boolean,
    transport: Transport
): BigInteger {
    return try {
        val result: GetUserOperationGasPriceResult = ModularApiImpl.getUserOperationGasPrice(transport)
        if (deployed) {
            result.deployed ?: fallbackGasLimit(deployed)
        } else {
            result.notDeployed ?: fallbackGasLimit(deployed)
        }
    } catch (e: Exception) {
        fallbackGasLimit(deployed)
    }
}

private fun fallbackGasLimit(deployed: Boolean): BigInteger {
    return if (deployed) MINIMUM_VERIFICATION_GAS_LIMIT else MINIMUM_UNDEPLOY_VERIFICATION_GAS_LIMIT
}
