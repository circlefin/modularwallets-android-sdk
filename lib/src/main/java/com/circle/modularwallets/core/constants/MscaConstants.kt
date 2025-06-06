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

package com.circle.modularwallets.core.constants

import com.circle.modularwallets.core.utils.data.pad
import org.web3j.utils.Numeric
import java.math.BigInteger

// Lowered to 100,000 due to pre-op gas limit efficiency issues on multiple testnets.
// See CCS-1984: https://circlepay.atlassian.net/browse/CCS-1984
val MINIMUM_VERIFICATION_GAS_LIMIT = BigInteger.valueOf(100_000)
val MINIMUM_UNDEPLOY_VERIFICATION_GAS_LIMIT = BigInteger.valueOf(1_500_000)

/** The Circle Upgradable MSCA Factory. */
object FACTORY {
    val abi = CIRCLE_MSCA_6900_V1_EP07_FACTORY_ABI
    val address = "0x0000000DF7E6c9Dc387cAFc5eCBfa6c3a6179AdD"
}

/** The upgradable MSCA account implementation */
object CIRCLE_WEIGHTED_WEB_AUTHN_MULTISIG_PLUGIN {
    val address = "0x0000000C984AFf541D6cE86Bb697e68ec57873C8"
    val manifestHash =
        Numeric.hexStringToByteArray("0xa043327d77a74c1c55cfa799284b831fe09535a88b9f5fa4173d334e5ba0fd91")
}

object REPLAY_SAFE_HASH_V1 {
    val name = "Weighted Multisig Webauthn Plugin"
    val primaryType = "CircleWeightedWebauthnMultisigMessage"
    val domainSeparatorType =
        "EIP712Domain(string name,string version,uint256 chainId,address verifyingContract,bytes32 salt)"
    val moduleType = "CircleWeightedWebauthnMultisigMessage(bytes32 hash)"
    val version = "1.0.0"
}

const val EIP712_PREFIX = "0x1901"
val EIP1271_VALID_SIGNATURE = byteArrayOf(0x16, 0x26, 0xba.toByte(), 0x7e)

/** The salt for the MSCA factory contract. */
internal val SALT = Numeric.hexStringToByteArray(pad("0x", 32))

/** The default owner weight. */
val OWNER_WEIGHT = 1L

/** The threshold weight. */
val THRESHOLD_WEIGHT = 1L
val STUB_SIGNATURE =
    "0x0000be58786f7ae825e097256fc83a4749b95189e03e9963348373e9c595b15200000000000000000000000000000000000000000000000000000000000000412200000000000000000000000000000000000000000000000000000000000002400000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000006091077742edaf8be2fa866827236532ec2a5547fe2721e606ba591d1ffae7a15c022e5f8fe5614bbf65ea23ad3781910eb04a1a60fae88190001ecf46e5f5680a00000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000001700000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000002549960de5880e8c687434170f6476605b8fe4aeb9a28632c7995cf3ba831d9763050000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000867b2274797065223a22776562617574686e2e676574222c226368616c6c656e6765223a224b6d62474d316a4d554b57794d6352414c6774553953537144384841744867486178564b6547516b503541222c226f726967696e223a22687474703a2f2f6c6f63616c686f73743a35313733222c2263726f73734f726967696e223a66616c73657d0000000000000000000000000000000000000000000000000000"
const val CIRCLE_SMART_ACCOUNT_VERSION_V1 = "circle_passkey_account_v1"
internal val CIRCLE_SMART_ACCOUNT_VERSION: Map<String, String> = mapOf(
    CIRCLE_SMART_ACCOUNT_VERSION_V1 to "circle_6900_v1"
)

/* Signature flag */
internal const val SIG_TYPE_FLAG_DIGEST = 32L
/* Base type */
internal const val SIG_TYPE_SECP256R1 = 2L
/* Digest-flagged type */
internal const val SIG_TYPE_SECP256R1_DIGEST = 34L