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


package com.circle.modularwallets.core.constants

// Ether units
internal val etherUnits: Map<String, Int> = mapOf(
    "gwei" to 9,
    "wei" to 18
)

// Gwei units
internal val gweiUnits: Map<String, Int> = mapOf(
    "ether" to -9,
    "wei" to 9
)

// Wei units
internal val weiUnits: Map<String, Int> = mapOf(
    "ether" to -18,
    "gwei" to -9
)