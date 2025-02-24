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


package com.circle.modularwallets.core.errors

import com.circle.modularwallets.core.models.UserOperation
import com.circle.modularwallets.core.models.UserOperationV07
import com.circle.modularwallets.core.utils.prettyPrint
import com.circle.modularwallets.core.utils.toMap
import com.circle.modularwallets.core.utils.unit.formatGwei
import java.math.BigInteger


// https://github.com/wevm/viem/blob/3866a6faeb9e64ba3da6063fe78a079ea53c2c5f/src/account-abstraction/errors/userOperation.ts#L104
class WaitForUserOperationReceiptTimeoutError(hash: String) : BaseError(
    "Timed out while waiting for User Operation with hash \"$hash\" to be confirmed.",
    BaseErrorParameters(name = "WaitForUserOperationReceiptTimeoutError")
)

// https://github.com/wevm/viem/blob/3866a6faeb9e64ba3da6063fe78a079ea53c2c5f/src/account-abstraction/errors/userOperation.ts#L80
class UserOperationReceiptNotFoundError(hash: String) : BaseError(
    "User Operation receipt with hash \"$hash\" could not be found. The User Operation may not have been processed yet.",
    BaseErrorParameters(name = "UserOperationReceiptNotFoundError")
)

//https://github.com/wevm/viem/blob/f34580367127be8ec02e2f1a9dbf5d81c29e74e8/src/account-abstraction/errors/userOperation.ts#L89C1-L99C1
class UserOperationNotFoundError(hash: String) : BaseError(
    "User Operation with hash \"$hash\" could not be found.",
    BaseErrorParameters(name = "UserOperationNotFoundError")
)

class UserOperationExecutionError private constructor(
    cause: BaseError,
    parameters: BaseErrorParameters
) : BaseError(cause.shortMessage, parameters) {
    constructor(cause: BaseError, userOp: UserOperationV07) : this(
        cause, BaseErrorParameters(
            cause,
            metaMessages = getMetaMessages(cause, getUserOpPrettyPrint(userOp)),
            name = "UserOperationExecutionError"
        )
    )

    companion object {
        inline fun <reified T : UserOperation> getUserOpPrettyPrint(userOp: T): String {
            val map = userOp.toMap().toMutableMap()
            map["maxFeePerGas"]?.let {
                val n: BigInteger? = if (it is BigInteger) it else null
                map["maxFeePerGas"] = "${formatGwei(n)} gwei"
            }
            map["maxPriorityFeePerGas"]?.let {
                val n: BigInteger? = if (it is BigInteger) it else null
                map["maxPriorityFeePerGas"] = "${formatGwei(n)} gwei"
            }
            return prettyPrint(map)
        }

        fun getMetaMessages(cause: BaseError, prettyArgs: String): MutableList<String> {
            val messages = mutableListOf<String>()
            cause.metaMessages?.let {
                messages.addAll(it)
            }


            if (messages.isNotEmpty() && !messages.last().endsWith("\n")) {
                messages[messages.lastIndex] = messages.last() + "\n"
            }

            messages.add("Request Arguments:")
            messages.add(prettyArgs)

            return messages.filter { it.isNotBlank() }.toMutableList()
        }
    }
}