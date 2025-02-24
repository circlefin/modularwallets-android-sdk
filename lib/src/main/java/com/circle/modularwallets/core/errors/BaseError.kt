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

import com.circle.modularwallets.core.BuildConfig

data class BaseErrorParameters(
    val cause: Throwable? = null,
    val details: String? = null,
    val metaMessages: MutableList<String>? = null,
    val name: String = "BaseError",
)

open class BaseError(
    open val shortMessage: String,
    args: BaseErrorParameters = BaseErrorParameters()
) : Exception(buildMessage(shortMessage, args), args.cause) {

    val details: String? = getDetails(args)
    val metaMessages: MutableList<String>? = args.metaMessages
    open val name: String = args.name

    companion object {
        private fun getDetails(args: BaseErrorParameters): String? {
            return when {
                args.cause is BaseError -> args.cause.details
                args.cause?.message != null -> args.cause.message
                else -> args.details
            }
        }

        private fun buildMessage(shortMessage: String, args: BaseErrorParameters): String {
            val messageParts = mutableListOf<String>()
            messageParts.add("${args.name}: " + (shortMessage.takeIf { it.isNotEmpty() }
                ?: "An error occurred."))
            if (messageParts.isNotEmpty() && !messageParts.last().endsWith("\n")) {
                messageParts[messageParts.lastIndex] = messageParts.last() + "\n"
            }

            args.metaMessages?.let {
                messageParts.addAll(it)
            }

            if (messageParts.isNotEmpty() && !messageParts.last().endsWith("\n")) {
                messageParts[messageParts.lastIndex] = messageParts.last() + "\n"
            }

            val details = getDetails(args)
            details?.let {
                messageParts.add("Details: $it")
            }

            messageParts.add("Version: ${BuildConfig.version}")

            return messageParts.joinToString("\n")
        }
    }

    fun walk(fn: ((Throwable?) -> Boolean)? = null): Throwable? {
        return walk(this, fn)
    }
}

fun walk(
    err: Throwable? = null,
    fn: ((Throwable?) -> Boolean)? = null
): Throwable? {
    if (fn?.invoke(err) == true) return err
    err?.cause?.let {
        return walk(it, fn)
    }
    return if (fn != null) null else err
}