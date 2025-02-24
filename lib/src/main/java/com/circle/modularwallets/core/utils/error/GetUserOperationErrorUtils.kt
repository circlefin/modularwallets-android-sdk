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

import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.errors.UserOperationExecutionError
import com.circle.modularwallets.core.models.UserOperationRpc
import com.circle.modularwallets.core.models.UserOperationV07
import com.circle.modularwallets.core.models.toUserOperationV07

internal fun getUserOperationError(
    err: BaseError,
    args: UserOperationRpc?,
): BaseError {
    val cause = getBundlerError(err, args)
    return UserOperationExecutionError(
        cause = cause,
        userOp = args?.toUserOperationV07() ?: UserOperationV07() //use empty as default
    )
}