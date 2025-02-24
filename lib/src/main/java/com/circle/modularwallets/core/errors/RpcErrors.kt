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


data class RpcErrorOptions(
    val code: Int? = null,
    val metaMessages: MutableList<String>? = null,
    val name: String? = null,
    val shortMessage: String,
)

open class RpcError(
    cause: Throwable,
    options: RpcErrorOptions,
) : BaseError(
    options.shortMessage, BaseErrorParameters(
        cause,
        metaMessages = getMetaMessage(options, cause),
        name = getName(options, cause)
    )
) {
    val code: Int = if (cause is RpcRequestError) {
        cause.code
    } else {
        options.code ?: UnknownRpcError.code
    }

    companion object {
        fun getMetaMessage(options: RpcErrorOptions, cause: Throwable): MutableList<String>? {
            options.metaMessages?.let {
                return it
            }
            if (cause is BaseError) {
                return cause.metaMessages
            }
            return null
        }

        fun getName(options: RpcErrorOptions, cause: Throwable): String {
            options.name?.let {
                return it
            }
            if (cause is BaseError) {
                return cause.name
            }
            return "RpcError"
        }
    }
}

open class ProviderRpcError(cause: Throwable, options: RpcErrorOptions) : RpcError(
    cause,
    options
)

class ParseRpcError(cause: Throwable) : RpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "ParseRpcError",
        shortMessage = "Invalid JSON was received by the server. An error occurred on the server while parsing the JSON text."
    )
) {
    companion object {
        const val code = -32700
    }
}

class InvalidRequestRpcError(cause: Throwable) : RpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "InvalidRequestRpcError",
        shortMessage = "JSON is not a valid request object."
    )
) {
    companion object {
        const val code = -32600
    }
}

class MethodNotFoundRpcError(cause: Throwable, method: String? = null) : RpcError(
    cause,
    RpcErrorOptions(code,
        name = "MethodNotFoundRpcError",
        shortMessage = "The method${method?.let { " \"$it\"" } ?: ""} does not exist / is not available.")
) {
    companion object {
        const val code = -32601
    }
}

class InvalidParamsRpcError(cause: Throwable) : RpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "InvalidParamsRpcError",
        shortMessage = "Invalid parameters were provided to the RPC method.\nDouble check you have provided the correct parameters."
    )
) {
    companion object {
        const val code = -32602
    }
}

class InternalRpcError(cause: Throwable) : RpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "InternalRpcError",
        shortMessage = "An internal error was received."
    )
) {
    companion object {
        const val code = -32603
    }
}

class InvalidInputRpcError(cause: Throwable) : RpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "InvalidInputRpcError",
        shortMessage = "Missing or invalid parameters.\nDouble check you have provided the correct parameters."
    )
) {
    companion object {
        const val code = -32000
    }
}

class ResourceNotFoundRpcError(cause: Throwable) : RpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "ResourceNotFoundRpcError",
        shortMessage = "Requested resource not found."
    )
) {
    companion object {
        const val code = -32001
    }
}

class ResourceUnavailableRpcError(cause: Throwable) : RpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "ResourceUnavailableRpcError",
        shortMessage = "Requested resource not available."
    )
) {
    companion object {
        const val code = -32002
    }
}

class TransactionRejectedRpcError(cause: Throwable) : RpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "TransactionRejectedRpcError",
        shortMessage = "Transaction creation failed."
    )
) {
    companion object {
        const val code = -32003
    }
}

class MethodNotSupportedRpcError(cause: Throwable, method: String? = null) : RpcError(
    cause,
    RpcErrorOptions(code,
        name = "MethodNotSupportedRpcError",
        shortMessage = "Method${method?.let { " \"$it\"" } ?: ""} is not implemented.")
) {
    companion object {
        const val code = -32004
    }
}

class LimitExceededRpcError(cause: Throwable) : RpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "LimitExceededRpcError",
        shortMessage = "Request exceeds defined limit."
    )
) {
    companion object {
        const val code = -32005
    }
}

class JsonRpcVersionUnsupportedError(cause: Throwable) : RpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "JsonRpcVersionUnsupportedError",
        shortMessage = "Version of JSON-RPC protocol is not supported."
    )
) {
    companion object {
        const val code = -32006
    }
}

class UserRejectedRequestError(cause: Throwable) : ProviderRpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "UserRejectedRequestError",
        shortMessage = "User rejected the request."
    )
) {
    companion object {
        const val code = 4001
    }
}

class UnauthorizedProviderError(cause: Throwable) : ProviderRpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "UnauthorizedProviderError",
        shortMessage = "The requested method and/or account has not been authorized by the user."
    )
) {
    companion object {
        const val code = 4100
    }
}

class UnsupportedProviderMethodError(cause: Throwable, method: String? = null) : ProviderRpcError(
    cause,
    RpcErrorOptions(code,
        name = "UnsupportedProviderMethodError",
        shortMessage = "The Provider does not support the requested method${method?.let { " \"$it\"" } ?: ""}.")
) {
    companion object {
        const val code = 4200
    }
}

class ProviderDisconnectedError(cause: Throwable) : ProviderRpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "ProviderDisconnectedError",
        shortMessage = "The Provider is disconnected from all chains."
    )
) {
    companion object {
        const val code = 4900
    }
}

class ChainDisconnectedError(cause: Throwable) : ProviderRpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "ChainDisconnectedError",
        shortMessage = "The Provider is not connected to the requested chain."
    )
) {
    companion object {
        const val code = 4901
    }
}

class SwitchChainError(cause: Throwable) : ProviderRpcError(
    cause,
    RpcErrorOptions(
        code,
        name = "SwitchChainError",
        shortMessage = "An error occurred when attempting to switch chain."
    )
) {
    companion object {
        const val code = 4902
    }
}

class UnknownRpcError(cause: Throwable) : RpcError(
    cause,
    RpcErrorOptions(
        name = "UnknownRpcError",
        shortMessage = "An unknown RPC error occurred."
    )
) {
    companion object {
        const val code = -1
    }
}