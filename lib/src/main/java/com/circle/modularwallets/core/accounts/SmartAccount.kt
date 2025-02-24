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

package com.circle.modularwallets.core.accounts

import android.content.Context
import com.circle.modularwallets.core.clients.Client
import com.circle.modularwallets.core.models.EncodeCallDataArg
import com.circle.modularwallets.core.models.EntryPoint
import com.circle.modularwallets.core.models.EstimateUserOperationGasResult
import com.circle.modularwallets.core.models.UserOperation
import com.circle.modularwallets.core.models.UserOperationV07
import java.math.BigInteger

/**
 * A Smart Account is an account whose implementation resides in a Smart Contract, and implements the ERC-4337 interface.
 *
 * @param client The client used to interact with the blockchain.
 * @param entryPoint The entry point for the smart account.
 */
abstract class SmartAccount(val client: Client, val entryPoint: EntryPoint) {

    /**
     * Configuration for the user operation.
     */
    open var userOperation: UserOperationConfiguration? = null

    /**
     * Returns the address of the account.
     *
     * @return The address of the smart account.
     */
    abstract fun getAddress(): String

    /**
     * Encodes the given call data arguments.
     *
     * @param args The call data arguments to encode.
     * @return The encoded call data.
     */
    abstract fun encodeCalls(args: Array<EncodeCallDataArg>): String

    /**
     * Returns the factory arguments for the smart account.
     *
     * @return A pair containing the factory arguments.
     */
    abstract suspend fun getFactoryArgs(): Pair<String, String>?

    /**
     * Returns the nonce for the smart account.
     *
     * @param key Optional key to retrieve the nonce for.
     * @return The nonce of the smart account.
     */
    abstract suspend fun getNonce(key: BigInteger? = null): BigInteger

    /**
     * Returns the stub signature for the given user operation.
     *
     * @param userOp The user operation to retrieve the stub signature for. Type T must be the subclass of UserOperation.
     * @return The stub signature.
     */
    abstract fun <T : UserOperation> getStubSignature(userOp: T): String

    /**
     * Signs the given hex data.
     *
     * @param context The context in which the signing operation is performed.
     * @param hex The hex data to sign.
     * @return The signed data.
     */
    abstract suspend fun sign(context: Context, hex: String): String

    /**
     * Signs the given message.
     *
     * @param context The context in which the signing operation is performed.
     * @param message The message to sign.
     * @return The signed message.
     */
    abstract suspend fun signMessage(context: Context, message: String): String

    /**
     * Signs the given typed data.
     *
     * @param context The context in which the signing operation is performed.
     * @param typedData The typed data to sign.
     * @return The signed typed data.
     */
    abstract suspend fun signTypedData(context: Context, typedData: String): String

    /**
     * Signs the given user operation.
     *
     * @param context The context in which the signing operation is performed.
     * @param chainId The chain ID for the user operation. Default is the chain ID of the client.
     * @param userOp The user operation to sign.
     * @return The signed user operation.
     */
    abstract suspend fun signUserOperation(
        context: Context,
        chainId: Long = client.chain.chainId,
        userOp: UserOperationV07
    ): String

    /**
     * Retrieves the initialization code for the smart account.
     *
     * @return The initialization code.
     */
    abstract suspend fun getInitCode(): String?
}

data class UserOperationConfiguration @JvmOverloads constructor(var estimateGas: (suspend (UserOperation) -> EstimateUserOperationGasResult)? = null)
