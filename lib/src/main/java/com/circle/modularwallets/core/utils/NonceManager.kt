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

package com.circle.modularwallets.core.utils

import java.math.BigInteger

data class FunctionParameters(val address: String, val chainId: Long)

interface NonceManagerSource {
    fun get(parameters: FunctionParameters): BigInteger
    fun set(parameters: FunctionParameters, nonce: BigInteger)
}

internal class NonceManager(private val source: NonceManagerSource) {
    private val deltaMap = mutableMapOf<String, BigInteger>()
    private val nonceMap = mutableMapOf<String, BigInteger>()

    private fun getKey(params: FunctionParameters) = "${params.address}.${params.chainId}"
    /**
     * Increase delta
     * Update nonceMap with value (source nonce or previousNonce + 1) + delta.
     * The value will be used as previousNonce next time.
     * */
    fun consume(params: FunctionParameters): BigInteger {
        val key = getKey(params)
        increment(params)
        val nonce = get(params)
        source.set(params, nonce)
        nonceMap[key] = nonce
        return nonce
    }
    /** Increase delta */
    private fun increment(params: FunctionParameters) {
        val key = getKey(params)
        val delta = deltaMap[key] ?: BigInteger.ZERO
        deltaMap[key] = delta.plus(BigInteger.ONE)
    }
    /** Return (source nonce or previousNonce + 1) + delta */
    fun get(params: FunctionParameters): BigInteger {
        val key = getKey(params)
        val delta = deltaMap[key] ?: BigInteger.ZERO
        val nonce = internalGet(params, key)
        return delta + nonce
    }
    /** Reset delta */
    private fun reset(params: FunctionParameters) {
        val key = getKey(params)
        deltaMap.remove(key)
    }
    /** Return source nonce or previousNonce + 1  */
    private fun internalGet(params: FunctionParameters, key: String): BigInteger {
        val nonce: BigInteger
        try {
            val fetchedNonce = source.get(params)
            val previousNonce = nonceMap[key] ?: BigInteger.ZERO
            if (previousNonce > BigInteger.ZERO && fetchedNonce <= previousNonce) {
                nonce = previousNonce.plus(BigInteger.ONE)
            } else {
                nonceMap.remove(key)
                nonce = fetchedNonce
            }
        } finally {
            reset(params)
        }
        return nonce
    }
}
