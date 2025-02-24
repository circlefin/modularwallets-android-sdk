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

package com.circle.modularwallets.core.utils.rpc

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.circle.modularwallets.core.BuildConfig
import java.security.MessageDigest

internal fun getAppInfo(context: Context): String {
    return "platform=android;version=${BuildConfig.version};package=${context.packageName};signature=${
        getSha256CertificateFingerprint(
            context
        )
    }"
}

internal fun getSha256CertificateFingerprint(context: Context): String? {
    try {
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_SIGNING_CERTIFICATES
        )
        val signature = packageInfo.signingInfo.apkContentsSigners[0]

        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(signature.toByteArray())
        val fingerprint = digest.joinToString(":") { String.format("%02X", it) }
        return fingerprint
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}