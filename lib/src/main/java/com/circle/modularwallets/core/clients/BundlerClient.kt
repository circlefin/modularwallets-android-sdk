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

package com.circle.modularwallets.core.clients

import android.content.Context
import com.circle.modularwallets.core.accounts.SmartAccount
import com.circle.modularwallets.core.annotation.ExcludeFromGeneratedCCReport
import com.circle.modularwallets.core.apis.bundler.BundlerApi
import com.circle.modularwallets.core.apis.bundler.BundlerApiImpl
import com.circle.modularwallets.core.apis.bundler.toResult
import com.circle.modularwallets.core.apis.bundler.toUserOperationReceipt
import com.circle.modularwallets.core.apis.modular.ModularApiImpl
import com.circle.modularwallets.core.apis.public.PublicApi
import com.circle.modularwallets.core.apis.public.PublicApiImpl
import com.circle.modularwallets.core.apis.util.UtilApi
import com.circle.modularwallets.core.apis.util.UtilApiImpl
import com.circle.modularwallets.core.chains.Chain
import com.circle.modularwallets.core.constants.CIRCLE_PLUGIN_ADD_OWNERS_ABI
import com.circle.modularwallets.core.constants.OWNER_WEIGHT
import com.circle.modularwallets.core.errors.BaseError
import com.circle.modularwallets.core.errors.ExecutionRevertedError
import com.circle.modularwallets.core.errors.InvalidParamsRpcError
import com.circle.modularwallets.core.errors.UserOperationExecutionError
import com.circle.modularwallets.core.models.AddressMappingOwner
import com.circle.modularwallets.core.models.Block
import com.circle.modularwallets.core.models.CreateAddressMappingResult
import com.circle.modularwallets.core.models.EOAIdentifier
import com.circle.modularwallets.core.models.EncodeCallDataArg
import com.circle.modularwallets.core.models.EntryPoint
import com.circle.modularwallets.core.models.EoaAddressMappingOwner
import com.circle.modularwallets.core.models.EstimateFeesPerGasResult
import com.circle.modularwallets.core.models.EstimateUserOperationGasResult
import com.circle.modularwallets.core.models.GetUserOperationResult
import com.circle.modularwallets.core.models.Paymaster
import com.circle.modularwallets.core.models.UserOperationReceipt
import com.circle.modularwallets.core.models.UserOperationV07
import com.circle.modularwallets.core.models.toRpcUserOperation
import com.circle.modularwallets.core.transports.Transport
import com.circle.modularwallets.core.utils.abi.encodeFunctionData
import com.circle.modularwallets.core.utils.abi.isAddress
import com.circle.modularwallets.core.utils.encoding.hexToLong
import com.circle.modularwallets.core.utils.error.isMappedError
import java.math.BigInteger

class BundlerClient(chain: Chain, transport: Transport) : Client(chain, transport) {
    private val api: BundlerApi = BundlerApiImpl
    private val pubApi: PublicApi = PublicApiImpl
    private val utilApi: UtilApi = UtilApiImpl

    /**
     * Estimates the gas values for a User Operation to be executed successfully.
     *
     * @param account The Account to use for User Operation execution.
     * @param calls The calls to execute in the User Operation.
     * @param paymaster Sets Paymaster configuration for the User Operation.
     * @param estimateFeesPerGas Prepares fee properties for the User Operation request. Not available in Java.
     * @return The estimated gas values for the User Operation.
     */

    @Throws(Exception::class)
    @JvmOverloads
    suspend fun estimateUserOperationGas(
        account: SmartAccount,
        calls: Array<EncodeCallDataArg>,
        paymaster: Paymaster? = null,
        estimateFeesPerGas: (suspend (SmartAccount, BundlerClient, UserOperationV07) -> EstimateFeesPerGasResult)? = null
    ): EstimateUserOperationGasResult {
        val userOp = when (account.entryPoint) {
            EntryPoint.V07 -> {
                api.prepareUserOperation(
                    transport,
                    account,
                    calls,
                    UserOperationV07(),
                    paymaster,
                    this,
                    estimateFeesPerGas
                )
            }
        }
        return api.estimateUserOperationGas(transport, userOp, account.entryPoint)
    }

    /**
     * Returns the chain ID associated with the current network
     *
     * @return The current chain ID.
     */

    @Throws(Exception::class)
    suspend fun getChainId(): Long {
        val result = api.getChainId(transport)
        return hexToLong(result)
    }

    /**
     * Returns the EntryPoints that the bundler supports.
     *
     * @return The EntryPoints that the bundler supports.
     */

    @Throws(Exception::class)
    suspend fun getSupportedEntryPoints(): ArrayList<String> {
        return api.getSupportedEntryPoints(transport)
    }

    /**
     * Retrieves information about a User Operation given a hash.
     *
     * @param userOpHash User Operation hash.
     * @return User Operation information.
     */

    @Throws(Exception::class)
    suspend fun getUserOperation(userOpHash: String): GetUserOperationResult {
        return api.getUserOperation(transport, userOpHash).toResult()
    }

    /**
     * Returns the User Operation Receipt given a User Operation hash.
     *
     * @param userOpHash User Operation hash.
     * @return The User Operation receipt.
     */

    @Throws(Exception::class)
    suspend fun getUserOperationReceipt(userOpHash: String): UserOperationReceipt {
        return api.getUserOperationReceipt(transport, userOpHash).toUserOperationReceipt()
    }

    /**
     * Broadcasts a User Operation to the Bundler.
     *
     * @param context The context used to launch any UI needed; use an activity context to make sure the UI will be launched within the same task stack
     * @param account The Account to use for User Operation execution.
     * @param calls The calls to execute in the User Operation
     * @param partialUserOp The partial User Operation to be completed
     * @param paymaster Sets Paymaster configuration for the User Operation.
     * @param estimateFeesPerGas Prepares fee properties for the User Operation request. Not available in Java.
     * @return The hash of the sent User Operation.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    @JvmOverloads
    suspend fun sendUserOperation(
        context: Context,
        account: SmartAccount,
        calls: Array<EncodeCallDataArg>?,
        partialUserOp: UserOperationV07 = UserOperationV07(),
        paymaster: Paymaster? = null,
        estimateFeesPerGas: (suspend (SmartAccount, BundlerClient, UserOperationV07) -> EstimateFeesPerGasResult)? = null
    ): String {
        if (!partialUserOp.signature.isNullOrBlank()) {
            return api.sendUserOperation(
                transport,
                partialUserOp.toRpcUserOperation(),
                account.entryPoint.address
            )
        }
        val userOp = when (account.entryPoint) {
            EntryPoint.V07 -> {
                api.prepareUserOperation(
                    transport,
                    account,
                    calls,
                    partialUserOp,
                    paymaster,
                    this,
                    estimateFeesPerGas
                )
            }
        }
        userOp.signature = account.signUserOperation(context, chain.chainId, userOp)
        return api.sendUserOperation(
            transport,
            userOp.toRpcUserOperation(),
            account.entryPoint.address
        )
    }

    /**
     * Registers a recovery address during the recovery process.
     *
     * @param context The context used to launch any UI needed; use an activity context to make sure the UI will be launched within the same task stack
     * @param account The Account to use for User Operation execution.
     * @param recoveryAddress The recovery address.
     * @param partialUserOp A partially constructed UserOperation object.
     *                      The `callData` field, if provided, will be **overwritten internally**
     *                      with the encoded `addOwners` call data based on `recoveryAddress`.
     * @param paymaster Sets Paymaster configuration for the User Operation.
     * @param estimateFeesPerGas Prepares fee properties for the User Operation request. Not available in Java.
     * @return The hash of the sent User Operation, or `null` if no operation was sent because the recovery address already exists.
     */
    @ExcludeFromGeneratedCCReport
    @Throws(Exception::class)
    @JvmOverloads
    suspend fun registerRecoveryAddress(
        context: Context,
        account: SmartAccount,
        recoveryAddress: String,
        partialUserOp: UserOperationV07 = UserOperationV07(),
        paymaster: Paymaster? = null,
        estimateFeesPerGas: (suspend (SmartAccount, BundlerClient, UserOperationV07) -> EstimateFeesPerGasResult)? = null
    ): String? {
        if(!isAddress(recoveryAddress)){
            throw BaseError("Invalid recovery address format")
        }
        /** Step 1: Create a mapping between the MSCA address and the recovery address */
        val owners: Array<AddressMappingOwner> = arrayOf(EoaAddressMappingOwner(EOAIdentifier(recoveryAddress)))
        try {
            createAddressMapping(account.getAddress(), owners)
        } catch (error: InvalidParamsRpcError){
            /**
             * Ignore "address mapping already exists" errors to ensure idempotency and allow safe retries.
             * This prevents inconsistent states between RPC calls and onchain transactions.
             */
            if(!isMappedError(error)){
                throw BaseError("Failed to register the recovery address. Please try again.")
            }
        }

        /** Step 2: Encode the function call for the userOp */
        val addOwnersData = encodeFunctionData(
            "addOwners",
            CIRCLE_PLUGIN_ADD_OWNERS_ABI,
            arrayOf(
                arrayOf(recoveryAddress), // recovery address
                arrayOf(OWNER_WEIGHT), // weightsToAdd
                emptyArray<Any>(), // publicKeyOwnersToAdd
                emptyArray<Any>(), // publicKeyWeightsToAdd
                0, // newThresholdWeight, 0 means no change
            )
        )

        /** Step 3: Send user operation to store the recovery address onchain */
        try {
            partialUserOp.callData = addOwnersData
            return sendUserOperation(
                context,
                account,
                calls = null,// Set to null since callData is assigned directly.
                partialUserOp,
                paymaster,
                estimateFeesPerGas
            )
        } catch (error: UserOperationExecutionError){
            if(error.details == ExecutionRevertedError.message){
                val isOwner = UtilApiImpl.isOwnerOf(transport, account.getAddress(), recoveryAddress)
                if(isOwner) return null
            }
            throw error
        }
    }

    /**
     * Prepares a User Operation for execution and fills in missing properties.
     *
     * @param account The Account to use for User Operation execution.
     * @param calls The calls to execute in the User Operation
     * @param partialUserOp The partial User Operation to be completed
     * @param paymaster Sets Paymaster configuration for the User Operation.
     * @param estimateFeesPerGas Prepares fee properties for the User Operation request. Not available in Java.
     * @return The prepared User Operation.
     */

    @Throws(Exception::class)
    @JvmOverloads
    suspend fun prepareUserOperation(
        account: SmartAccount,
        calls: Array<EncodeCallDataArg>?,
        partialUserOp: UserOperationV07,
        paymaster: Paymaster? = null,
        estimateFeesPerGas: (suspend (SmartAccount, BundlerClient, UserOperationV07) -> EstimateFeesPerGasResult)? = null
    ): UserOperationV07 {
        return api.prepareUserOperation(
            transport,
            account,
            calls,
            partialUserOp,
            paymaster,
            this,
            estimateFeesPerGas
        )
    }

    /**
     * Waits for the User Operation to be included on a Block (one confirmation), and then returns the User Operation receipt.
     *
     * @param userOpHash A User Operation hash.
     * @param pollingInterval Polling frequency (in ms).
     * @param retryCount The number of times to retry.
     * @param timeout Optional timeout (in ms) to wait before stopping polling.
     * @return The User Operation receipt.
     */

    @Throws(Exception::class)
    @JvmOverloads
    suspend fun waitForUserOperationReceipt(
        userOpHash: String,
        pollingInterval: Long = 4000,
        retryCount: Int = 6,
        timeout: Long? = null
    ): UserOperationReceipt {
        return api.waitForUserOperationReceipt(
            transport,
            userOpHash,
            pollingInterval,
            retryCount,
            timeout
        ).toUserOperationReceipt()
    }

    /**
     * Retrieves the balance of the specified address at a given block tag.
     *
     * @param address The address to query the balance for. Only wallet addresses that registered with the using client key can be retrieved
     * @param blockNumber The balance of the account at a block number.
     * @return The balance of the address in wei.
     */

    @Throws(Exception::class)
    suspend fun getBalance(address: String, blockNumber: BigInteger): BigInteger {
        val result = pubApi.getBalance(transport, address, blockNumber)
        return result
    }

    /**
     * Retrieves the balance of the specified address at a given block tag.
     *
     * @param address The address to query the balance for. Only wallet addresses that registered with the using client key can be retrieved
     * @param blockTag The balance of the account at a block tag.
     * @return The balance of the address in wei.
     */

    @Throws(Exception::class)
    @JvmOverloads
    suspend fun getBalance(address: String, blockTag: String = "latest"): BigInteger {
        val result = pubApi.getBalance(transport, address, blockTag)
        return result
    }

    /**
     * Returns the number of the most recent block seen.
     *
     * @return The number of the block.
     */

    @Throws(Exception::class)
    suspend fun getBlockNumber(): BigInteger {
        val result = pubApi.getBlockNum(transport)
        return result
    }

    /**
     * Returns the current price of gas (in wei).
     *
     * @return The gas price (in wei).
     */

    @Throws(Exception::class)
    suspend fun getGasPrice(): BigInteger {
        val result = pubApi.getGasPrice(transport)
        return result
    }

    /**
     * Executes a new message call immediately without submitting a transaction to the network.
     *
     * @param from The Account to call from.
     * @param to The contract address or recipient.
     * @param data A contract hashed method call with encoded args.
     * @return The result of the call.
     */

    @Throws(Exception::class)
    suspend fun call(
        from: String?,
        to: String,
        data: String
    ): String {
        return pubApi.call(transport, from, to, data)
    }

    /**
     * Retrieves the bytecode at an address.
     *
     * @param address The contract address.
     * @param blockNumber The block number to perform the bytecode read against.
     * @return The code of the specified address at the given block number.
     */

    @Throws(Exception::class)
    suspend fun getCode(
        address: String,
        blockNumber: BigInteger
    ): String {
        return pubApi.getCode(transport, address, blockNumber)
    }

    /**
     * Retrieves the bytecode at an address.
     *
     * @param address The contract address.
     * @param blockTag The block tag to perform the bytecode read against.
     * @return The code of the specified address at the given block tag.
     */

    @Throws(Exception::class)
    @JvmOverloads
    suspend fun getCode(
        address: String,
        blockTag: String = "latest"
    ): String {
        return pubApi.getCode(transport, address, blockTag)
    }

    /**
     * Returns an estimate for the max priority fee per gas (in wei) for a transaction to be likely included in the next block.
     * The Action will either call eth_maxPriorityFeePerGas (if supported) or manually calculate the max priority fee per gas based on the current block base fee per gas + gas price.
     *
     * @return An estimate (in wei) for the max priority fee per gas.
     */

    @Throws(Exception::class)
    suspend fun estimateMaxPriorityFeePerGas(
    ): BigInteger {
        return utilApi.getMaxPriorityFeePerGas(transport)
    }

    /**
     * Returns information about a block at a given block number.
     *
     * @param includeTransactions Whether or not to include transactions (as a structured array of Transaction objects). Default is false.
     * @param blockNumber The block number to query the information for.
     * @return Information about the block.
     */

    @Throws(Exception::class)
    @JvmOverloads
    suspend fun getBlock(
        includeTransactions: Boolean = false,
        blockNumber: BigInteger
    ): Block {
        return pubApi.getBlock(transport, includeTransactions, blockNumber)
    }

    /**
     * Returns information about a block at a given block tag.
     *
     * @param includeTransactions Whether or not to include transactions (as a structured array of Transaction objects). Default is false.
     * @param blockTag The block tag to query the information for. Default is "latest".
     * @return Information about the block.
     */

    @Throws(Exception::class)
    @JvmOverloads
    suspend fun getBlock(
        includeTransactions: Boolean = false,
        blockTag: String = "latest"
    ): Block {
        return pubApi.getBlock(transport, includeTransactions, blockTag)
    }

    /**
     * Creates an address mapping for recovery.
     *
     * @param walletAddress The Circle smart wallet address.
     * @param owners The owners of the wallet.
     * @return The response from adding an address mapping.
     */
    @Throws(Exception::class)
    @JvmOverloads
    suspend fun createAddressMapping(
        walletAddress: String,
        owners: Array<AddressMappingOwner>
    ): Array<CreateAddressMappingResult> {
        return ModularApiImpl.createAddressMapping(transport, walletAddress, owners)
    }
}