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


package com.circle.modularwallets.core.utils.userOperation

import com.circle.modularwallets.core.models.EntryPoint
import com.circle.modularwallets.core.models.UserOperationV07
import com.circle.modularwallets.core.utils.data.concat
import com.circle.modularwallets.core.utils.data.pad
import com.circle.modularwallets.core.utils.encoding.bigIntegerToHex
import org.web3j.abi.DefaultFunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Hash
import org.web3j.utils.Numeric
import java.math.BigInteger

/**
 * Generates the hash of a user operation.
 *
 * @param chainId The ID of the blockchain network.
 * @param entryPointAddress The address of the entry point contract. Default is the address of EntryPoint.V07.
 * @param userOp The user operation to hash.
 * @return The hash of the user operation as a hex string.
 */

@JvmOverloads
fun getUserOperationHash(
    chainId: Long,
    entryPointAddress: String = EntryPoint.V07.address,
    userOp: UserOperationV07
): String {
    val accountGasLimits = Numeric.hexStringToByteArray(
        concat(
            pad(bigIntegerToHex(userOp.verificationGasLimit), 16),
            pad(bigIntegerToHex(userOp.callGasLimit), 16)
        )
    )
    val callDataHashed = Hash.sha3(Numeric.hexStringToByteArray(userOp.callData))
    val gasFees = Numeric.hexStringToByteArray(
        concat(
            pad(bigIntegerToHex(userOp.maxPriorityFeePerGas), 16),
            pad(bigIntegerToHex(userOp.maxFeePerGas), 16)
        )
    )
    val initCodeHashed = Hash.sha3(
        Numeric.hexStringToByteArray(
            if (userOp.factory != null && userOp.factoryData != null) concat(
                userOp.factory!!,
                userOp.factoryData!!
            ) else "0x"
        )
    )
    val paymasterAndDataHashed = Hash.sha3(Numeric.hexStringToByteArray(userOp.paymaster?.let {
        concat(
            it,
            pad(bigIntegerToHex(userOp.paymasterVerificationGasLimit ?: BigInteger.ZERO), 16),
            pad(bigIntegerToHex(userOp.paymasterPostOpGasLimit ?: BigInteger.ZERO), 16),
            userOp.paymasterData ?: "0x"
        )
    } ?: "0x"))
    val encoder = DefaultFunctionEncoder()
    val packedUserOp = encoder.encodeParameters(
        listOf<Type<*>>(
            Address(userOp.sender),
            Uint256(userOp.nonce),
            Bytes32(initCodeHashed),
            Bytes32(callDataHashed),
            Bytes32(accountGasLimits),
            Uint256(userOp.preVerificationGas),
            Bytes32(gasFees),
            Bytes32(paymasterAndDataHashed),
        )
    )
    return Hash.sha3(
        encoder.encodeParameters(
            listOf<Type<*>>(
                Bytes32(Hash.sha3(Numeric.hexStringToByteArray(packedUserOp))),
                Address(entryPointAddress),
                Uint256(chainId)
            )
        )
    )
}


internal fun parseFactoryAddressAndData(initCode: String): Pair<String, String> {
    val factoryAddress = initCode.substring(0, 42)
    val factoryData = ("0x${initCode.substring(42)}")
    return Pair(factoryAddress, factoryData)
}
