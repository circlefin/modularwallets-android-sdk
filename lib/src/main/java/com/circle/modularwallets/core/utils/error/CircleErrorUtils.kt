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

package com.circle.modularwallets.core.utils.error

import com.circle.modularwallets.core.errors.InvalidParamsRpcError

/**
 * Error description string for a known backend RPC error.
 *
 * Refer to backend error definition:
 * ExtStatusCodeConflictWalletIdentifierMapping
 *
 * This string is used to detect cases where the wallet-to-identifier mapping
 * already exists and should be optionally ignored by clients.
 */
const val ERROR_DESC_CONFLICT_WALLET_IDENTIFIER_MAPPING =
    "The wallet to identifier map already exists"

/**
 * Determines whether the given error represents an already-existing wallet-to-identifier mapping.
 * Matches error detail string based on backend-defined message.
 */
internal fun isMappedError(err: Exception): Boolean {
    if (err is InvalidParamsRpcError) {
        return err.details?.contains(ERROR_DESC_CONFLICT_WALLET_IDENTIFIER_MAPPING, ignoreCase = true) == true
    }
    return false
}