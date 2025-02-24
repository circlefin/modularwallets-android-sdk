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

interface IEntryPoint {
    val address: String
}

/**
 * Enum class representing entry points with their respective addresses.
 *
 * @property address The address of the entry point.
 */
enum class EntryPoint(override val address: String) :IEntryPoint{
    /**
     * Represents the entry point version 0.7 with its respective address.
     */
    V07("0x0000000071727De22E5E9d8BAf0edAc6f37da032");
}