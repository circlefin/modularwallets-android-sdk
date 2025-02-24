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

import com.circle.modularwallets.core.errors.AccountNotDeployedError
import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.errors.ExecutionRevertedError
import com.circle.modularwallets.core.errors.FailedToSendToBeneficiaryError
import com.circle.modularwallets.core.errors.GasValuesOverflowError
import com.circle.modularwallets.core.errors.HandleOpsOutOfGasError
import com.circle.modularwallets.core.errors.InitCodeFailedError
import com.circle.modularwallets.core.errors.InitCodeMustCreateSenderError
import com.circle.modularwallets.core.errors.InitCodeMustReturnSenderError
import com.circle.modularwallets.core.errors.InsufficientPrefundError
import com.circle.modularwallets.core.errors.InternalCallOnlyError
import com.circle.modularwallets.core.errors.InvalidAccountNonceError
import com.circle.modularwallets.core.errors.InvalidAggregatorError
import com.circle.modularwallets.core.errors.InvalidBeneficiaryError
import com.circle.modularwallets.core.errors.InvalidFieldsError
import com.circle.modularwallets.core.errors.InvalidPaymasterAndDataError
import com.circle.modularwallets.core.errors.PaymasterDepositTooLowError
import com.circle.modularwallets.core.errors.PaymasterFunctionRevertedError
import com.circle.modularwallets.core.errors.PaymasterNotDeployedError
import com.circle.modularwallets.core.errors.PaymasterPostOpFunctionRevertedError
import com.circle.modularwallets.core.errors.PaymasterRateLimitError
import com.circle.modularwallets.core.errors.PaymasterStakeTooLowError
import com.circle.modularwallets.core.errors.RpcRequestError
import com.circle.modularwallets.core.errors.SenderAlreadyConstructedError
import com.circle.modularwallets.core.errors.SignatureCheckFailedError
import com.circle.modularwallets.core.errors.SmartAccountFunctionRevertedError
import com.circle.modularwallets.core.errors.UnknownBundlerError
import com.circle.modularwallets.core.errors.UnsupportedSignatureAggregatorError
import com.circle.modularwallets.core.errors.UserOperationExpiredError
import com.circle.modularwallets.core.errors.UserOperationOutOfTimeRangeError
import com.circle.modularwallets.core.errors.UserOperationPaymasterExpiredError
import com.circle.modularwallets.core.errors.UserOperationPaymasterSignatureError
import com.circle.modularwallets.core.errors.UserOperationRejectedByEntryPointError
import com.circle.modularwallets.core.errors.UserOperationRejectedByOpCodeError
import com.circle.modularwallets.core.errors.UserOperationRejectedByPaymasterError
import com.circle.modularwallets.core.errors.UserOperationSignatureError
import com.circle.modularwallets.core.errors.VerificationGasLimitExceededError
import com.circle.modularwallets.core.errors.VerificationGasLimitTooLowError
import com.circle.modularwallets.core.models.UserOperationRpc
import com.circle.modularwallets.core.utils.encoding.hexToBigInteger

private val bundlerErrors = listOf(
    ExecutionRevertedError(),
    InvalidFieldsError(),
    PaymasterDepositTooLowError(),
    PaymasterRateLimitError(),
    PaymasterStakeTooLowError(),
    SignatureCheckFailedError(),
    UnsupportedSignatureAggregatorError(),
    UserOperationOutOfTimeRangeError(),
    UserOperationRejectedByEntryPointError(),
    UserOperationRejectedByPaymasterError(),
    UserOperationRejectedByOpCodeError(),
)

internal fun getBundlerError(
    err: BaseError,
    args: UserOperationRpc?
): BaseError {
    val message = (err.details ?: "").lowercase()
    val firstResult = when {
        AccountNotDeployedError.message.toRegex().containsMatchIn(message) -> {
            AccountNotDeployedError(cause = err)
        }

        FailedToSendToBeneficiaryError.message.toRegex().containsMatchIn(message) -> {
            FailedToSendToBeneficiaryError(cause = err)
        }

        GasValuesOverflowError.message.toRegex().containsMatchIn(message) -> {
            GasValuesOverflowError(cause = err)
        }

        HandleOpsOutOfGasError.message.toRegex().containsMatchIn(message) -> {
            HandleOpsOutOfGasError(cause = err)
        }

        InitCodeFailedError.message.toRegex().containsMatchIn(message) -> {
            InitCodeFailedError(
                cause = err,
                factory = args?.factory,
                factoryData = args?.factoryData,
                initCode = args?.initCode
            )
        }

        InitCodeMustCreateSenderError.message.toRegex().containsMatchIn(message) -> {
            InitCodeMustCreateSenderError(
                cause = err,
                factory = args?.factory,
                factoryData = args?.factoryData,
                initCode = args?.initCode
            )
        }

        InitCodeMustReturnSenderError.message.toRegex().containsMatchIn(message) -> {
            InitCodeMustReturnSenderError(
                err,
                factory = args?.factory,
                factoryData = args?.factoryData,
                initCode = args?.initCode,
                sender = args?.sender
            )
        }


        InsufficientPrefundError.message.toRegex().containsMatchIn(message) -> {
            InsufficientPrefundError(cause = err)
        }

        InternalCallOnlyError.message.toRegex().containsMatchIn(message) -> {
            InternalCallOnlyError(cause = err)
        }

        InvalidAccountNonceError.message.toRegex().containsMatchIn(message) -> {
            InvalidAccountNonceError(
                err,
                nonce = hexToBigInteger(args?.nonce)
            )
        }

        InvalidAggregatorError.message.toRegex().containsMatchIn(message) -> {
            InvalidAggregatorError(cause = err)
        }

        InvalidBeneficiaryError.message.toRegex().containsMatchIn(message) -> {
            InvalidBeneficiaryError(cause = err)
        }

        InvalidPaymasterAndDataError.message.toRegex().containsMatchIn(message) -> {
            InvalidPaymasterAndDataError(cause = err)
        }

        PaymasterDepositTooLowError.message.toRegex().containsMatchIn(message) -> {
            PaymasterDepositTooLowError(cause = err)
        }

        PaymasterFunctionRevertedError.message.toRegex().containsMatchIn(message) -> {
            PaymasterFunctionRevertedError(cause = err)
        }

        PaymasterNotDeployedError.message.toRegex().containsMatchIn(message) -> {
            PaymasterNotDeployedError(cause = err)
        }

        PaymasterPostOpFunctionRevertedError.message.toRegex().containsMatchIn(message) -> {
            PaymasterPostOpFunctionRevertedError(cause = err)
        }

        SmartAccountFunctionRevertedError.message.toRegex().containsMatchIn(message) -> {
            SmartAccountFunctionRevertedError(cause = err)
        }

        SenderAlreadyConstructedError.message.toRegex().containsMatchIn(message) -> {
            SenderAlreadyConstructedError(
                cause = err,
                factory = args?.factory,
                factoryData = args?.factoryData,
                initCode = args?.initCode
            )
        }

        UserOperationExpiredError.message.toRegex().containsMatchIn(message) -> {
            UserOperationExpiredError(cause = err)
        }

        UserOperationPaymasterExpiredError.message.toRegex().containsMatchIn(message) -> {
            UserOperationPaymasterExpiredError(cause = err)
        }

        UserOperationPaymasterSignatureError.message.toRegex().containsMatchIn(message) -> {
            UserOperationPaymasterSignatureError(cause = err)
        }

        UserOperationSignatureError.message.toRegex().containsMatchIn(message) -> {
            UserOperationSignatureError(cause = err)
        }

        VerificationGasLimitExceededError.message.toRegex().containsMatchIn(message) -> {
            VerificationGasLimitExceededError(cause = err)
        }

        VerificationGasLimitTooLowError.message.toRegex().containsMatchIn(message) -> {
            VerificationGasLimitTooLowError(cause = err)
        }

        else -> null
    }

    if (firstResult != null) {
        return firstResult
    }

    if (err is RpcRequestError) {
        val rpcErrorCode = err.code
        val secondResult = bundlerErrors.firstOrNull { error ->
            when (error) {
                is ExecutionRevertedError -> ExecutionRevertedError.code == rpcErrorCode
                is InvalidFieldsError -> InvalidFieldsError.code == rpcErrorCode
                is PaymasterDepositTooLowError -> PaymasterDepositTooLowError.code == rpcErrorCode
                is PaymasterRateLimitError -> PaymasterRateLimitError.code == rpcErrorCode
                is PaymasterStakeTooLowError -> PaymasterStakeTooLowError.code == rpcErrorCode
                is SignatureCheckFailedError -> SignatureCheckFailedError.code == rpcErrorCode
                is UnsupportedSignatureAggregatorError -> UnsupportedSignatureAggregatorError.code == rpcErrorCode
                is UserOperationOutOfTimeRangeError -> UserOperationOutOfTimeRangeError.code == rpcErrorCode
                is UserOperationRejectedByEntryPointError -> UserOperationRejectedByEntryPointError.code == rpcErrorCode
                is UserOperationRejectedByPaymasterError -> UserOperationRejectedByPaymasterError.code == rpcErrorCode
                is UserOperationRejectedByOpCodeError -> UserOperationRejectedByOpCodeError.code == rpcErrorCode
                else -> false
            }
        }
        if (secondResult != null) {
            when (secondResult) {
                is ExecutionRevertedError -> {
                    return ExecutionRevertedError(
                        cause = err,
                        message = err.details
                    )
                }

                is InvalidFieldsError -> {
                    return InvalidFieldsError(cause = err)
                }

                is PaymasterDepositTooLowError -> {
                    return PaymasterDepositTooLowError(cause = err)
                }

                is PaymasterRateLimitError -> {
                    return PaymasterRateLimitError(cause = err)
                }

                is PaymasterStakeTooLowError -> {
                    return PaymasterStakeTooLowError(cause = err)
                }

                is SignatureCheckFailedError -> {
                    return SignatureCheckFailedError(cause = err)
                }

                is UnsupportedSignatureAggregatorError -> {
                    return UnsupportedSignatureAggregatorError(cause = err)
                }

                is UserOperationOutOfTimeRangeError -> {
                    return UserOperationOutOfTimeRangeError(cause = err)
                }

                is UserOperationRejectedByEntryPointError -> {
                    return UserOperationRejectedByEntryPointError(cause = err)
                }

                is UserOperationRejectedByPaymasterError -> {
                    return UserOperationRejectedByPaymasterError(cause = err)
                }

                is UserOperationRejectedByOpCodeError -> {
                    return UserOperationRejectedByOpCodeError(cause = err)
                }
            }
        }
    }

    return UnknownBundlerError(err)
}
