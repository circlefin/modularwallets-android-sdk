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

import com.circle.modularwallets.core.utils.abi.encodeTransfer

/**
 * Enum representing various tokens supported by [encodeTransfer].
 * The format is {chain}_{symbol}
 */
enum class Token {
    Arbitrum_USDC,
    Arbitrum_ARB,
    ArbitrumSepolia_USDC,
    Base_USDC,
    BaseSepolia_USDC,
    Optimism_USDC,
    Optimism_OP,
    OptimismSepolia_USDC,
    Polygon_USDC,
    PolygonAmoy_USDC,
    Unichain_USDC,
    UnichainSepolia_USDC;
}
