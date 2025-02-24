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

package com.circle.modularwallets.core.apis.paymaster

import com.circle.modularwallets.core.models.IEntryPoint
import com.circle.modularwallets.core.models.UserOperation
import com.circle.modularwallets.core.models.UserOperationV07
import com.circle.modularwallets.core.models.toRpcUserOperation
import com.circle.modularwallets.core.transports.RpcRequest
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.rpc.performJsonRpcRequest
import okhttp3.internal.toHexString

internal object PaymasterApiImpl : PaymasterApi {
    override suspend fun <T : UserOperation> getPaymasterData(
        transport: Transport,
        userOp: T,
        entryPoint: IEntryPoint,
        chainId: Long,
        context: Map<String, Any>?
    ): GetPaymasterDataResp {
        val rpcUserOp =
            if (userOp is UserOperationV07) userOp.toRpcUserOperation() else userOp.toRpcUserOperation()
        if (rpcUserOp.callGasLimit == null) {
            rpcUserOp.callGasLimit = "0x0"
        }
        if (rpcUserOp.verificationGasLimit == null) {
            rpcUserOp.verificationGasLimit = "0x0"
        }
        if (rpcUserOp.preVerificationGas == null) {
            rpcUserOp.preVerificationGas = "0x0"
        }
        val param = if (context == null) listOf(
            rpcUserOp,
            entryPoint.address,
            "0x${chainId.toHexString()}",
        ) else listOf(
            rpcUserOp,
            entryPoint.address,
            "0x${chainId.toHexString()}",
            context
        )
        val jsonRpcReq = RpcRequest(
            "pm_getPaymasterData",
            param
        )
        val result = performJsonRpcRequest(transport, jsonRpcReq, GetPaymasterDataResp::class.java)
        return result.first
    }

    override suspend fun <T : UserOperation> getPaymasterStubData(
        transport: Transport,
        userOp: T,
        entryPoint: IEntryPoint,
        chainId: Long,
        context: Map<String, Any>?
    ): GetPaymasterStubDataResp {
        val rpcUserOp =
            if (userOp is UserOperationV07) userOp.toRpcUserOperation() else userOp.toRpcUserOperation()
        if (rpcUserOp.callGasLimit == null) {
            rpcUserOp.callGasLimit = "0x0"
        }
        if (rpcUserOp.verificationGasLimit == null) {
            rpcUserOp.verificationGasLimit = "0x0"
        }
        if (rpcUserOp.preVerificationGas == null) {
            rpcUserOp.preVerificationGas = "0x0"
        }

        val param = if (context == null) listOf(
            rpcUserOp,
            entryPoint.address,
            "0x${chainId.toHexString()}",
        ) else listOf(
            rpcUserOp,
            entryPoint.address,
            "0x${chainId.toHexString()}",
            context
        )
        val jsonRpcReq = RpcRequest(
            "pm_getPaymasterStubData", param
        )
        val result =
            performJsonRpcRequest(transport, jsonRpcReq, GetPaymasterStubDataResp::class.java)
        return result.first
    }
}