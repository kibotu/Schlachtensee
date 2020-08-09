
##### Show verbose logs #####
-verbose

##### Allow repackaging #####
-repackageclasses

-keep class net.kibotu.schlachtensee.** { *; }
-keep interface net.kibotu.schlachtensee.** { *; }
-keepclassmembers class net.kibotu.schlachtensee.** { *; }

-dontwarn com.google.android.material.**
-keep class com.google.android.material.** { *; }

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }

-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }

# Enum types
-keepclassmembers enum * { *; }


# Firebase
-keepattributes Signature

# Crashlytics

-keepattributes *Annotation*

-keepattributes SourceFile,LineNumberTable

-keep public class * extends java.lang.Exception

-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

# Glide

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# ok http
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# okio
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*


# retrofit
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.-KotlinExtensions

########--------Retrofit + RxJava--------#########
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-dontwarn sun.misc.Unsafe
-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
-dontwarn retrofit.appengine.UrlFetchClient
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn retrofit.**

-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}



##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##---------------End: proguard configuration for Gson  ----------

# gmaps


# gmaps

# gmaps

-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }

-optimizations !code/simplification/variable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment

# https://github.com/googlemaps/android-samples/blob/master/ApiDemos/java/app/proguard-rules.pro

# The Maps API uses custom Parcelables.
# Use this rule (which is slightly broader than the standard recommended one)
# to avoid obfuscating them.
-keepclassmembers class * implements android.os.Parcelable {
    static *** CREATOR;
}

# The Maps API uses serialization.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# glide https://github.com/bumptech/glide#proguard
# https://github.com/bumptech/glide/issues/2821

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl { *; }

-keep class com.europapark.services.glide.GlideApp { *; }


# Keep all JNI utils.
-keep class com.google.android.apps.gmm.map.util.jni.** {
    *;
}

# The native methods in GeometryUtil should never be stripped even when unused.
-keep class com.google.android.apps.gmm.map.internal.vector.gl.GeometryUtil {
  *;
}

# The native methods in NativeVertexDataBuilder should never be stripped even when unused. Otherwise,
# the app will crash at startup with a NoSuchMethodError while trying to register the native methods
# that have been stripped.
-keepclassmembers class com.google.android.apps.gmm.map.internal.vector.gl.NativeVertexDataBuilder {
    private native void nativeFinalize(...);
    private native void nativeSetCoordScale(...);
}

# We use @UsedByReflection and @UsedByNative annotations internally to guard
# some classes/methods that Proguard should never optimize out.
-keep class com.google.maps.api.android.lib6.UsedBy*
-keep @com.google.maps.api.android.lib6.UsedBy* class *
-keepclassmembers class * {
    @com.google.maps.api.android.lib6.UsedBy* *;
}

# Required to construct the Cronet Engine Builder.
-keep class org.chromium.net.impl.** { <init>(...); }

# Keep the SQLite cache native methods.
-keepclassmembers class com.google.android.apps.gmm.map.internal.store.diskcache.NativeSqliteDiskCacheImpl {
  private static native <methods>;
}

# Dont obfuscate Google code to help with debugging.
-keep,allowshrinking,allowoptimization class com.google.** {
  *;
}

-dontwarn com.google.android.libraries.maps.**
-dontnote com.google.android.libraries.maps.**

# We keep all fields for every generated proto file as the runtime uses
# reflection over them that ProGuard cannot detect. Without this keep
# rule, fields may be removed that would cause runtime failures.
-keepclassmembers class * extends com.google.android.libraries.maps.kn.zzat {
  <fields>;
}

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
-dontobfuscate

-keep class com.EuropaParkMackKG.EPGuide.** { *; }
-keep class com.europapark.** { *; }

############ Crashlytics ################################
-keepattributes *Annotation*
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keepattributes SourceFile,LineNumberTable

### okhttp https://github.com/square/okhttp/blob/master/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-keep class android.support.v8.renderscript.** { *; }

-dontwarn javax.**
-dontwarn io.realm.**

# Retrofit
-keep class com.google.gson.** { *; }
-keep public class com.google.gson.** {public private protected *;}
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.xml.stream.** { *; }
-keep class retrofit.** { *; }
-keep class com.google.appengine.** { *; }
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn com.squareup.okhttp.*
-dontwarn rx.**
-dontwarn javax.xml.stream.**
-dontwarn com.google.appengine.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.**


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##---------------End: proguard configuration for Gson  ----------

-keep class androidx.core.app.CoreComponentFactory { *; }

# gmaps

-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }

-optimizations !code/simplification/variable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment

# https://github.com/googlemaps/android-samples/blob/master/ApiDemos/java/app/proguard-rules.pro

# The Maps API uses custom Parcelables.
# Use this rule (which is slightly broader than the standard recommended one)
# to avoid obfuscating them.
-keepclassmembers class * implements android.os.Parcelable {
    static *** CREATOR;
}

# The Maps API uses serialization.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# glide https://github.com/bumptech/glide#proguard
# https://github.com/bumptech/glide/issues/2821

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl { *; }

-keep class com.europapark.services.glide.GlideApp { *; }


# Keep all JNI utils.
-keep class com.google.android.apps.gmm.map.util.jni.** {
    *;
}

# The native methods in GeometryUtil should never be stripped even when unused.
-keep class com.google.android.apps.gmm.map.internal.vector.gl.GeometryUtil {
  *;
}

# The native methods in NativeVertexDataBuilder should never be stripped even when unused. Otherwise,
# the app will crash at startup with a NoSuchMethodError while trying to register the native methods
# that have been stripped.
-keepclassmembers class com.google.android.apps.gmm.map.internal.vector.gl.NativeVertexDataBuilder {
    private native void nativeFinalize(...);
    private native void nativeSetCoordScale(...);
}

# We use @UsedByReflection and @UsedByNative annotations internally to guard
# some classes/methods that Proguard should never optimize out.
-keep class com.google.maps.api.android.lib6.UsedBy*
-keep @com.google.maps.api.android.lib6.UsedBy* class *
-keepclassmembers class * {
    @com.google.maps.api.android.lib6.UsedBy* *;
}

# Required to construct the Cronet Engine Builder.
-keep class org.chromium.net.impl.** { <init>(...); }

# Keep the SQLite cache native methods.
-keepclassmembers class com.google.android.apps.gmm.map.internal.store.diskcache.NativeSqliteDiskCacheImpl {
  private static native <methods>;
}

# Dont obfuscate Google code to help with debugging.
-keep,allowshrinking,allowoptimization class com.google.** {
  *;
}

-dontwarn com.google.android.libraries.maps.**
-dontnote com.google.android.libraries.maps.**

# We keep all fields for every generated proto file as the runtime uses
# reflection over them that ProGuard cannot detect. Without this keep
# rule, fields may be removed that would cause runtime failures.
-keepclassmembers class * extends com.google.android.libraries.maps.kn.zzat {
  <fields>;
}




# Fix maps 3.0.0-beta crash:
-keep,allowoptimization class com.google.android.libraries.maps.** { *; }

# Fix maps 3.0.0-beta marker taps ignored:
-keep,allowoptimization class com.google.android.apps.gmm.renderer.** { *; }

-keepattributes Signature,InnerClasses
-ignorewarnings
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# https://proguard-rules.blogspot.com/2017/06/simplexml-proguard-rules.html

# SimpleXML
-keep interface org.simpleframework.xml.core.Label {
   public *;
}
-keep class * implements org.simpleframework.xml.core.Label {
   public *;
}
-keep interface org.simpleframework.xml.core.Parameter {
   public *;
}
-keep class * implements org.simpleframework.xml.core.Parameter {
   public *;
}
-keep interface org.simpleframework.xml.core.Extractor {
   public *;
}
-keep class * implements org.simpleframework.xml.core.Extractor {
   public *;
}

