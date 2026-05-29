# Consumer ProGuard rules — applied by R8 in the consumer app's build.
#
# These rules ship with the AAR (via consumerProguardFiles in lib/build.gradle.kts)
# and run in addition to whatever rules the consumer app supplies.
#
# Keep narrow targets only. Each block must have a comment explaining why it
# is load-bearing — without that, future maintainers can't tell whether the
# rule is still needed.

# Preserve the JVM Signature attribute. Jackson reads it at runtime to recover
# generic type information for collection-typed properties — notably
# EIP712Message.types : MutableMap<String, MutableList<Entry>>. Without it,
# Jackson sees the setter as raw `Map`/`List`, deserializes the nested type
# entries as LinkedHashMap, and downstream code (StructuredDataEncoder) then
# ClassCastExceptions when iterating them as Entry. InnerClasses and
# EnclosingMethod are paired per R8's own recommendation — Signature can
# reference inner classes, and stripping the other two leaves dangling refs.
# The default proguard-android(-optimize).txt usually keeps these, but
# consumer R8 configurations vary, so we set them explicitly.
-keepattributes Signature, InnerClasses, EnclosingMethod

# EIP-712 typed-data DTOs are deserialized by Jackson via reflection
# (see StructuredDataEncoder.parseJSONMessage). Jackson is brought in
# transitively via web3j; jackson-module-kotlin is NOT registered, so
# Jackson falls back to JavaBean-style binding and needs each property's
# public setter to remain reachable. Without these rules, R8 in consumer
# apps strips the setters as unused (Jackson's reflective access is
# invisible to R8) and verifyingContract / chainId / etc. fail to bind
# with `UnrecognizedPropertyException`.
#
# Scope: constructors + getters + setters only. Data-class extras
# (componentN / copy / equals / hashCode / toString) are not on Jackson's
# binding path and can be shrunk/obfuscated freely.
-keepclassmembers class com.circle.modularwallets.core.models.EIP712Domain {
    <init>(...);
    public *** get*();
    public void set*(***);
}
-keepclassmembers class com.circle.modularwallets.core.models.EIP712Message {
    <init>(...);
    public *** get*();
    public void set*(***);
}
-keepclassmembers class com.circle.modularwallets.core.models.Entry {
    <init>(...);
    public *** get*();
    public void set*(***);
}

# web3j (transitive dependency, used internally by StructuredDataEncoder for
# EIP-712 typed-data hashing). web3j resolves ABI type classes by reflection
# (`Class.forName("org.web3j.abi.datatypes." + name)` in AbiTypes/TypeEncoder),
# so consumer R8 must not rename or strip those datatype classes. Without this
# rule, `hashTypedData(json)` fails at runtime with
# `BaseError: Received an invalid argument for which no constructor exists for the ABI Class …`
# in any minified consumer app. The crypto and utils packages are not reflective
# — SDK code directly imports the entry points (Hash, Sign, Numeric, …) so R8's
# tracer keeps everything reachable through them. No broad `crypto.**` keep
# needed.
-keep class org.web3j.abi.datatypes.** { *; }
-dontwarn org.web3j.**
# web3j drags in SLF4J as a logging facade. Consumer apps that don't ship a
# concrete SLF4J binding would otherwise R8-error on the missing impl class.
-dontwarn org.slf4j.**
