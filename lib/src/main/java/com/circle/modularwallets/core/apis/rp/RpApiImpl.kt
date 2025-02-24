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


package com.circle.modularwallets.core.apis.rp

import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.models.AuthenticationCredential
import com.circle.modularwallets.core.models.RegistrationCredential
import com.circle.modularwallets.core.transports.RpcRequest
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.rpc.performJsonRpcRequest
import com.circle.modularwallets.core.utils.toJson

internal class RpApiImpl(val transport: Transport) : RpApi {
    override suspend fun getRegistrationOptions(userName: String): Pair<PublicKeyCredentialCreationOptions, String> {
        val req = RpcRequest("rp_getRegistrationOptions", listOf<Any>(userName))
        val result =
            performJsonRpcRequest(transport, req, PublicKeyCredentialCreationOptions::class.java)
        return Pair(
            result.first,
            toJson(result.first)
        )
    }

    @ExcludeFromGeneratedCCReport
    override suspend fun getRegistrationVerification(registrationCredential: RegistrationCredential): Boolean {
        val req = RpcRequest(
            "rp_getRegistrationVerification",
            listOf<Any>(registrationCredential)
        )
        return performJsonRpcRequest(transport, req) as Boolean
    }

    override suspend fun getLoginOptions(): Pair<PublicKeyCredentialRequestOptions, String> {
        val req = RpcRequest("rp_getLoginOptions")
        val result =
            performJsonRpcRequest(transport, req, PublicKeyCredentialRequestOptions::class.java)
        return Pair(
            result.first,
            toJson(result.first)
        )
    }

    @ExcludeFromGeneratedCCReport
    override suspend fun getLoginVerification(authenticationCredential: AuthenticationCredential): String {
        val req =
            RpcRequest("rp_getLoginVerification", listOf(authenticationCredential))
        val result = performJsonRpcRequest(transport, req, GetLoginVerificationResp::class.java)
        return result.first.publicKey
    }
}