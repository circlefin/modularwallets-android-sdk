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

import java.math.BigInteger

open class AccountNotDeployedError(
    cause: BaseError? = null,
) : BaseError(
    "Smart Account is not deployed.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- No `factory`/`factoryData` or `initCode` properties are provided for Smart Account deployment.",
            "- An incorrect `sender` address is provided.",
        ),
        name = "AccountNotDeployedError"
    )
) {
    companion object {
        const val message = "aa20"
    }
}

open class ExecutionRevertedError(
    cause: BaseError? = null,
    message: String? = null
) : BaseError(
    getMessage(message),
    BaseErrorParameters(
        cause,
        name = "ExecutionRevertedError"
    )
) {
    companion object {
        const val code: Int = -32521
        const val message = "execution reverted"
        fun getMessage(message: String? = null): String {

            var reason = message
                ?.replace("execution reverted: ", "")
                ?.replace("execution reverted", "")
            reason =
                if (reason?.isNotEmpty() == true) "with reason: $reason" else "for an unknown reason"
            return "Execution reverted $reason."
        }
    }

}

open class FailedToSendToBeneficiaryError(
    cause: BaseError? = null,
) : BaseError(
    "Failed to send funds to beneficiary.",
    BaseErrorParameters(
        cause,
        name = "FailedToSendToBeneficiaryError"
    )
) {
    companion object {
        const val message = "aa91"
    }
}

open class GasValuesOverflowError(
    cause: BaseError? = null,
) : BaseError(
    "Gas value overflowed.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- one of the gas values exceeded 2**120 (uint120)",
        ),
        name = "GasValuesOverflowError"
    )
) {
    companion object {
        const val message = "aa94"
    }
}

open class HandleOpsOutOfGasError(
    cause: BaseError? = null,
) : BaseError(
    "The `handleOps` function was called by the Bundler with a gas limit too low.",
    BaseErrorParameters(
        cause,
        name = "HandleOpsOutOfGasError"
    )
) {
    companion object {
        const val message = "aa95"
    }
}

open class InitCodeFailedError(
    cause: BaseError? = null,
    factory: String? = null,
    factoryData: String? = null,
    initCode: String? = null,
) : BaseError(
    "Failed to simulate deployment for Smart Account.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- Invalid `factory`/`factoryData` or `initCode` properties are present",
            "- Smart Account deployment execution ran out of gas (low `verificationGasLimit` value)",
            "- Smart Account deployment execution reverted with an error",
        ).apply {
            val rare = lastIndex
            if (isNotEmpty() && !this[lastIndex].endsWith("\n")) {
                this[lastIndex] = this[lastIndex] + "\n"
            }

            factory?.let { add("factory: $it") }
            factoryData?.let { add("factoryData: $it") }
            initCode?.let { add("initCode: $it") }

            if (isNotEmpty() && rare != lastIndex && !this[lastIndex].endsWith("\n")) {
                this[lastIndex] = this[lastIndex] + "\n"
            }
        },
        name = "InitCodeFailedError"
    )
) {
    companion object {
        const val message = "aa13"
    }
}

open class InitCodeMustCreateSenderError(
    cause: BaseError? = null,
    factory: String? = null,
    factoryData: String? = null,
    initCode: String? = null,
) : BaseError(
    "Smart Account initialization implementation did not create an account.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- `factory`/`factoryData` or `initCode` properties are invalid",
            "- Smart Account initialization implementation is incorrect\n",
        ).apply {
            factory?.let { add("factory: $it") }
            factoryData?.let { add("factoryData: $it") }
            initCode?.let { add("initCode: $it") }
        },
        name = "InitCodeMustCreateSenderError"
    )
) {
    companion object {
        const val message = "aa15"
    }
}

open class InitCodeMustReturnSenderError(
    cause: BaseError? = null,
    factory: String? = null,
    factoryData: String? = null,
    initCode: String? = null,
    sender: String? = null,
) : BaseError(
    "Smart Account initialization implementation does not return the expected sender.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "Smart Account initialization implementation does not return a sender address\n",
        ).apply {
            factory?.let { add("factory: $it") }
            factoryData?.let { add("factoryData: $it") }
            initCode?.let { add("initCode: $it") }
            sender?.let { add("sender: $it") }
        },
        name = "InitCodeMustReturnSenderError"
    )
) {
    companion object {
        const val message = "aa14"
    }
}

open class InsufficientPrefundError(
    cause: BaseError? = null,
) : BaseError(
    "Smart Account does not have sufficient funds to execute the User Operation.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- the Smart Account does not have sufficient funds to cover the required prefund, or",
            "- a Paymaster was not provided",
        ),
        name = "InsufficientPrefundError"
    )
) {
    companion object {
        const val message = "aa21"
    }
}

open class InternalCallOnlyError(
    cause: BaseError? = null,
) : BaseError(
    "Bundler attempted to call an invalid function on the EntryPoint.",
    BaseErrorParameters(
        cause,
        name = "InternalCallOnlyError"
    )
) {
    companion object {
        const val message = "aa92"
    }
}

open class InvalidAggregatorError(
    cause: BaseError? = null,
) : BaseError(
    "Bundler used an invalid aggregator for handling aggregated User Operations.",
    BaseErrorParameters(
        cause,
        name = "InvalidAggregatorError"
    )
) {
    companion object {
        const val message = "aa96"
    }
}

open class InvalidAccountNonceError(
    cause: BaseError? = null,
    nonce: BigInteger? = null,
) : BaseError(
    "Invalid Smart Account nonce used for User Operation.",
    BaseErrorParameters(
        cause,
        metaMessages = nonce?.let {
            mutableListOf("nonce: $it")
        },
        name = "InvalidAccountNonceError"
    )
) {
    companion object {
        const val message = "aa25"
    }
}

open class InvalidBeneficiaryError(
    cause: BaseError? = null,
) : BaseError(
    "Bundler has not set a beneficiary address.",
    BaseErrorParameters(
        cause,
        name = "InvalidBeneficiaryError"
    )
) {
    companion object {
        const val message = "aa90"
    }
}

open class InvalidFieldsError(
    cause: BaseError? = null,
) : BaseError(
    "Invalid fields set on User Operation.",
    BaseErrorParameters(
        cause,
        name = "InvalidFieldsError"
    )
) {
    companion object {
        const val code: Int = -32602
    }
}

open class InvalidPaymasterAndDataError(
    cause: BaseError? = null,
    paymasterAndData: String? = null,
) : BaseError(
    "Paymaster properties provided are invalid.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- the `paymasterAndData` property is of an incorrect length\n",
        ).apply {
            paymasterAndData?.let { add("paymasterAndData: $it") }
        },
        name = "InvalidPaymasterAndDataError"
    )
) {
    companion object {
        const val message = "aa93"
    }
}

open class PaymasterDepositTooLowError(
    cause: BaseError? = null,
) : BaseError(
    "Paymaster deposit for the User Operation is too low.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- the Paymaster has deposited less than the expected amount via the `deposit` function",
        ),
        name = "PaymasterDepositTooLowError"
    )
) {
    companion object {
        const val code: Int = -32508
        const val message = "aa31"
    }

}

open class PaymasterFunctionRevertedError(
    cause: BaseError? = null,
) : BaseError(
    "The `validatePaymasterUserOp` function on the Paymaster reverted.",
    BaseErrorParameters(
        cause,
        name = "PaymasterFunctionRevertedError"
    )
) {
    companion object {
        const val message = "aa33"
    }
}

open class PaymasterNotDeployedError(
    cause: BaseError? = null,
) : BaseError(
    "The Paymaster contract has not been deployed.",
    BaseErrorParameters(
        cause,
        name = "PaymasterNotDeployedError"
    )
) {
    companion object {
        const val message = "aa30"
    }
}

open class PaymasterRateLimitError(
    cause: BaseError? = null,
) : BaseError(
    "UserOperation rejected because paymaster (or signature aggregator) is throttled/banned.",
    BaseErrorParameters(
        cause,
        name = "PaymasterRateLimitError"
    )
) {
    companion object {
        const val code: Int = -32504
    }
}

open class PaymasterStakeTooLowError(
    cause: BaseError? = null,
) : BaseError(
    "UserOperation rejected because paymaster (or signature aggregator) is throttled/banned.",
    BaseErrorParameters(
        cause,
        name = "PaymasterStakeTooLowError"
    )
) {
    companion object {
        const val code: Int = -32505
    }
}

open class PaymasterPostOpFunctionRevertedError(
    cause: BaseError? = null,
) : BaseError(
    "Paymaster `postOp` function reverted.",
    BaseErrorParameters(
        cause,
        name = "PaymasterPostOpFunctionRevertedError"
    )
) {
    companion object {
        const val message = "aa50"
    }
}

open class SenderAlreadyConstructedError(
    cause: BaseError? = null,
    factory: String? = null,
    factoryData: String? = null,
    initCode: String? = null,
) : BaseError(
    "Smart Account has already been deployed.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "Remove the following properties and try again:",
        ).apply {
            factory?.let { add("`factory`") }
            factoryData?.let { add("`factoryData`") }
            initCode?.let { add("`initCode`") }
        },
        name = "SenderAlreadyConstructedError"
    )
) {
    companion object {
        const val message = "aa10"
    }
}

open class SignatureCheckFailedError(
    cause: BaseError? = null,
) : BaseError(
    "UserOperation rejected because account signature check failed (or paymaster signature, if the paymaster uses its data as signature).",
    BaseErrorParameters(
        cause,
        name = "SignatureCheckFailedError"
    )
) {
    companion object {
        const val code: Int = -32507
    }
}

open class SmartAccountFunctionRevertedError(
    cause: BaseError? = null,
) : BaseError(
    "The `validateUserOp` function on the Smart Account reverted.",
    BaseErrorParameters(
        cause,
        name = "SmartAccountFunctionRevertedError"
    )
) {
    companion object {
        const val message = "aa23"
    }
}

open class UnsupportedSignatureAggregatorError(
    cause: BaseError? = null,
) : BaseError(
    "UserOperation rejected because account specified unsupported signature aggregator.",
    BaseErrorParameters(
        cause,
        name = "UnsupportedSignatureAggregatorError"
    )
) {
    companion object {
        const val code: Int = -32506
    }
}

open class UserOperationExpiredError(
    cause: BaseError? = null,
) : BaseError(
    "User Operation expired.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- the `validAfter` or `validUntil` values returned from `validateUserOp` on the Smart Account are not satisfied",
        ),
        name = "UserOperationExpiredError"
    )
) {
    companion object {
        const val message = "aa22"
    }
}

open class UserOperationPaymasterExpiredError(
    cause: BaseError? = null,
) : BaseError(
    "Paymaster for User Operation expired.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- the `validAfter` or `validUntil` values returned from `validatePaymasterUserOp` on the Paymaster are not satisfied",
        ),
        name = "UserOperationPaymasterExpiredError"
    )
) {
    companion object {
        const val message = "aa32"
    }
}

open class UserOperationSignatureError(
    cause: BaseError? = null,
) : BaseError(
    "Signature provided for the User Operation is invalid.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- the `signature` for the User Operation is incorrectly computed, and unable to be verified by the Smart Account",
        ),
        name = "UserOperationSignatureError"
    )
) {
    companion object {
        const val message = "aa24"
    }
}

open class UserOperationPaymasterSignatureError(
    cause: BaseError? = null,
) : BaseError(
    "Signature provided for the User Operation is invalid.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- the `signature` for the User Operation is incorrectly computed, and unable to be verified by the Paymaster",
        ),
        name = "UserOperationPaymasterSignatureError"
    )
) {
    companion object {
        const val message = "aa34"
    }
}

open class UserOperationRejectedByEntryPointError(
    cause: BaseError? = null,
) : BaseError(
    "User Operation rejected by EntryPoint's `simulateValidation` during account creation or validation.",
    BaseErrorParameters(
        cause,
        name = "UserOperationRejectedByEntryPointError"
    )
) {
    companion object {
        const val code: Int = -32500
    }
}

open class UserOperationRejectedByPaymasterError(
    cause: BaseError? = null,
) : BaseError(
    "User Operation rejected by Paymaster's `validatePaymasterUserOp`.",
    BaseErrorParameters(
        cause,
        name = "UserOperationRejectedByPaymasterError"
    )
) {
    companion object {
        const val code: Int = -32501
    }
}

open class UserOperationRejectedByOpCodeError(
    cause: BaseError? = null,
) : BaseError(
    "User Operation rejected with op code validation error.",
    BaseErrorParameters(
        cause,
        name = "UserOperationRejectedByOpCodeError"
    )
) {
    companion object {
        const val code: Int = -32502
    }
}

open class UserOperationOutOfTimeRangeError(
    cause: BaseError? = null,
) : BaseError(
    "UserOperation out of time-range: either wallet or paymaster returned a time-range, and it is already expired (or will expire soon).",
    BaseErrorParameters(
        cause,
        name = "UserOperationOutOfTimeRangeError"
    )
) {
    companion object {
        const val code: Int = -32503
    }
}

open class UnknownBundlerError(
    cause: BaseError? = null,
) : BaseError(
    "An error occurred while executing user operation: ${cause?.shortMessage}",
    BaseErrorParameters(
        cause,
        name = "UnknownBundlerError"
    )
)

open class VerificationGasLimitExceededError(
    cause: BaseError? = null,
) : BaseError(
    "User Operation verification gas limit exceeded.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- the gas used for verification exceeded the `verificationGasLimit`",
        ),
        name = "VerificationGasLimitExceededError"
    )
) {
    companion object {
        const val message = "aa40"
    }
}

open class VerificationGasLimitTooLowError(
    cause: BaseError? = null,
) : BaseError(
    "User Operation verification gas limit is too low.",
    BaseErrorParameters(
        cause,
        metaMessages = mutableListOf(
            "This could arise when:",
            "- the `verificationGasLimit` is too low to verify the User Operation",
        ),
        name = "VerificationGasLimitTooLowError"
    )
) {
    companion object {
        const val message = "aa41"
    }
}