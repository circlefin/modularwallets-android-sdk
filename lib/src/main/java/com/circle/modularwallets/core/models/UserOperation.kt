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

import com.circle.modularwallets.core.utils.encoding.bigIntegerToHex
import com.circle.modularwallets.core.utils.encoding.hexToBigInteger
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigInteger

/**
 * Abstract class representing a user operation.
 *
 * @property sender The address of the sender.
 * @property nonce The nonce of the operation.
 * @property callData The data to be sent in the call.
 * @property callGasLimit The gas limit for the call.
 * @property verificationGasLimit The gas limit for verification.
 * @property preVerificationGas The gas used before verification.
 * @property maxPriorityFeePerGas The maximum priority fee per gas.
 * @property maxFeePerGas The maximum fee per gas.
 * @property signature The signature of the operation.
 */
abstract class UserOperation {
    abstract var sender: String?
    abstract var nonce: BigInteger?
    abstract var callData: String?
    abstract var callGasLimit: BigInteger?
    abstract var verificationGasLimit: BigInteger?
    abstract var preVerificationGas: BigInteger?
    abstract var maxPriorityFeePerGas: BigInteger?
    abstract var maxFeePerGas: BigInteger?
    abstract var signature: String?
}

/**
 * Data class representing a user operation for version 0.7.
 *
 * @property sender The address of the sender.
 * @property nonce The nonce of the operation.
 * @property callData The data to be sent in the call.
 * @property callGasLimit The gas limit for the call.
 * @property verificationGasLimit The gas limit for verification.
 * @property preVerificationGas The gas used before verification.
 * @property maxPriorityFeePerGas The maximum priority fee per gas.
 * @property maxFeePerGas The maximum fee per gas.
 * @property signature The signature of the operation.
 * @property factory The factory address.
 * @property factoryData The data for the factory.
 * @property paymaster The paymaster address.
 * @property paymasterVerificationGasLimit The gas limit for paymaster verification.
 * @property paymasterPostOpGasLimit The gas limit for paymaster post-operation.
 * @property paymasterData The data for the paymaster.
 */
@JsonClass(generateAdapter = true)
data class UserOperationV07 @JvmOverloads constructor(
    @Json(name = "sender") override var sender: String? = null,
    @Json(name = "nonce") override var nonce: BigInteger? = null,
    @Json(name = "callData") override var callData: String? = null,
    @Json(name = "callGasLimit") override var callGasLimit: BigInteger? = null,
    @Json(name = "verificationGasLimit") override var verificationGasLimit: BigInteger? = null,
    @Json(name = "preVerificationGas") override var preVerificationGas: BigInteger? = null,
    @Json(name = "maxPriorityFeePerGas") override var maxPriorityFeePerGas: BigInteger? = null,
    @Json(name = "maxFeePerGas") override var maxFeePerGas: BigInteger? = null,
    @Json(name = "signature") override var signature: String? = null,
    @Json(name = "factory") var factory: String? = null,
    @Json(name = "factoryData") var factoryData: String? = null,
    @Json(name = "paymaster") var paymaster: String? = null,
    @Json(name = "paymasterVerificationGasLimit") var paymasterVerificationGasLimit: BigInteger? = null,
    @Json(name = "paymasterPostOpGasLimit") var paymasterPostOpGasLimit: BigInteger? = null,
    @Json(name = "paymasterData") var paymasterData: String? = null
) : UserOperation()

/**
 * Converts a `UserOperationV07` instance to a `UserOperationRpc` instance.
 *
 * @return A `UserOperationRpc` instance with the corresponding properties from the `UserOperationV07` instance.
 */
fun UserOperationV07.toRpcUserOperation(): UserOperationRpc {
    return UserOperationRpc(
        sender = this.sender,
        nonce = bigIntegerToHex(this.nonce),
        callData = this.callData,
        callGasLimit = bigIntegerToHex(this.callGasLimit),
        verificationGasLimit = bigIntegerToHex(this.verificationGasLimit),
        preVerificationGas = bigIntegerToHex(this.preVerificationGas),
        maxPriorityFeePerGas = bigIntegerToHex(this.maxPriorityFeePerGas),
        maxFeePerGas = bigIntegerToHex(this.maxFeePerGas),
        signature = this.signature,
        factory = this.factory,
        factoryData = this.factoryData,
        paymaster = this.paymaster,
        paymasterData = this.paymasterData,
        paymasterVerificationGasLimit = bigIntegerToHex(this.paymasterVerificationGasLimit),
        paymasterPostOpGasLimit = bigIntegerToHex(this.paymasterPostOpGasLimit),
    )
}

/**
 * Converts a `UserOperation` instance to a `UserOperationRpc` instance.
 *
 * @return A `UserOperationRpc` instance with the corresponding properties from the `UserOperation` instance.
 */
fun UserOperation.toRpcUserOperation(): UserOperationRpc {
    return UserOperationRpc(
        sender = this.sender,
        nonce = bigIntegerToHex(this.nonce),
        callData = this.callData,
        callGasLimit = bigIntegerToHex(this.callGasLimit),
        verificationGasLimit = bigIntegerToHex(this.verificationGasLimit),
        preVerificationGas = bigIntegerToHex(this.preVerificationGas),
        maxPriorityFeePerGas = bigIntegerToHex(this.maxPriorityFeePerGas),
        maxFeePerGas = bigIntegerToHex(this.maxFeePerGas),
        signature = this.signature
    )
}

/**
 * Converts a `UserOperationRpc` instance to a `UserOperationV07` instance.
 *
 * @return A `UserOperationV07` instance with the corresponding properties from the `UserOperationRpc` instance.
 */
fun UserOperationRpc.toUserOperationV07(): UserOperationV07 {
    return UserOperationV07(
        sender = this.sender,
        nonce = hexToBigInteger(this.nonce),
        callData = this.callData,
        callGasLimit = hexToBigInteger(this.callGasLimit),
        verificationGasLimit = hexToBigInteger(this.verificationGasLimit),
        preVerificationGas = hexToBigInteger(this.preVerificationGas),
        maxPriorityFeePerGas = hexToBigInteger(this.maxPriorityFeePerGas),
        maxFeePerGas = hexToBigInteger(this.maxFeePerGas),
        signature = this.signature,
        factory = this.factory,
        factoryData = this.factoryData,
        paymaster = this.paymaster,
        paymasterVerificationGasLimit = hexToBigInteger(this.paymasterVerificationGasLimit),
    )
}

/**
 * Data class representing a user operation in RPC format.
 *
 * @property sender The address of the sender.
 * @property nonce The nonce of the operation.
 * @property callData The data to be sent in the call.
 * @property callGasLimit The gas limit for the call.
 * @property verificationGasLimit The gas limit for verification.
 * @property preVerificationGas The gas used before verification.
 * @property maxPriorityFeePerGas The maximum priority fee per gas.
 * @property maxFeePerGas The maximum fee per gas.
 * @property signature The signature of the operation.
 * @property factory The factory address.
 * @property factoryData The data for the factory.
 * @property paymaster The paymaster address.
 * @property paymasterVerificationGasLimit The gas limit for paymaster verification.
 * @property paymasterPostOpGasLimit The gas limit for paymaster post-operation.
 * @property paymasterData The data for the paymaster.
 * @property initCode The initialization code.
 * @property paymasterAndData The paymaster and data.
 */
@JsonClass(generateAdapter = true)
data class UserOperationRpc @JvmOverloads constructor(
    @Json(name = "sender") var sender: String? = null,
    @Json(name = "nonce") var nonce: String? = null,
    @Json(name = "callData") var callData: String? = null,
    @Json(name = "callGasLimit") var callGasLimit: String? = null,
    @Json(name = "verificationGasLimit") var verificationGasLimit: String? = null,
    @Json(name = "preVerificationGas") var preVerificationGas: String? = null,
    @Json(name = "maxPriorityFeePerGas") var maxPriorityFeePerGas: String? = null,
    @Json(name = "maxFeePerGas") var maxFeePerGas: String? = null,
    @Json(name = "signature") var signature: String? = null,
    @Json(name = "factory") var factory: String? = null,
    @Json(name = "factoryData") var factoryData: String? = null,
    @Json(name = "paymaster") var paymaster: String? = null,
    @Json(name = "paymasterVerificationGasLimit") var paymasterVerificationGasLimit: String? = null,
    @Json(name = "paymasterPostOpGasLimit") var paymasterPostOpGasLimit: String? = null,
    @Json(name = "paymasterData") var paymasterData: String? = null,
    @Json(name = "initCode") var initCode: String? = null,
    @Json(name = "paymasterAndData") var paymasterAndData: String? = null
)