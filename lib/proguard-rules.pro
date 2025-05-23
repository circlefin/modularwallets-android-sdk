# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# https://developer.android.com/identity/sign-in/credential-manager#proguard
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}
-keep public class com.circle.modularwallets.core.** {
    public *;
}
-keep class com.circle.modularwallets.core.apis.rp.PublicKeyCredentialCreationOptions { *; }
-keep class com.circle.modularwallets.core.apis.rp.AuthenticatorSelectionCriteria { *; }
-keep class com.circle.modularwallets.core.apis.rp.PublicKeyCredentialRpEntity { *; }
-keep class com.circle.modularwallets.core.apis.rp.PublicKeyCredentialUserEntity { *; }
-keep class com.circle.modularwallets.core.apis.rp.PublicKeyCredentialRequestOptions { *; }
-keep class com.circle.modularwallets.core.apis.rp.PublicKeyCredentialDescriptor { *; }
-keep class com.circle.modularwallets.core.apis.rp.PublicKeyCredentialParameters { *; }
-keep class androidx.credentials.** { *; }
-keep class org.json.** { *; }