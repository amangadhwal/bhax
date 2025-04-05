# Media playback libraries
-keep class androidx.media3.** { *; }
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**

# GPU Image processing
-keep class jp.co.cyberagent.android.gpuimage.** { *; }
-keep class jp.co.cyberagent.android.gpuimage.gl.** { *; }

# Glide image loading
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.** { *; }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Animation libraries
-keep class com.airbnb.lottie.** { *; }
-keep class com.facebook.shimmer.** { *; }
-keep class com.daimajia.** { *; }
-dontwarn com.daimajia.**

# RenderScript
-keep class androidx.renderscript.** { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}

# OpenGL specific rules
-keepclasseswithmembernames class * {
    void glGenTextures(int, int[], int);
    void glDeleteTextures(int, int[], int);
    void glBindTexture(int, int);
    void glTexImage2D(int, int, int, int, int, int, int, int, java.nio.Buffer);
}

# Custom shader effects
-keep class com.bhax.app.ui.ritual.shaders.** { *; }
-keepclassmembers class com.bhax.app.ui.ritual.shaders.** {
    <fields>;
    <methods>;
}

# Effect configurations
-keep class com.bhax.app.ui.ritual.config.** {
    <fields>;
    <methods>;
}

# Dynamic animation components
-keep class androidx.dynamicanimation.animation.** { *; }
-keep class * extends androidx.dynamicanimation.animation.Force
-keep class * extends androidx.dynamicanimation.animation.DynamicAnimation

# Ritual effect interfaces
-keep interface com.bhax.app.ui.ritual.effects.** { *; }
-keep class * implements com.bhax.app.ui.ritual.effects.** { *; }

# Performance monitoring
-keep class com.bhax.app.util.performance.** { *; }
-keepclassmembers class com.bhax.app.util.performance.** {
    public <methods>;
}

# Debug tools - strip in release
-assumenosideeffects class com.bhax.app.util.RitualLogger {
    public static void d(...);
    public static void v(...);
}

# Keep ritual state and configuration
-keepclassmembers class com.bhax.app.ui.ritual.state.** {
    <fields>;
}

# Video effect processors
-keep class com.bhax.app.ui.ritual.video.** { *; }
-keepclassmembers class com.bhax.app.ui.ritual.video.** {
    native <methods>;
    void process(...);
    void applyEffect(...);
}

# Effect metadata
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations

# Resource optimizations
-keep class **.R$raw { *; }
-keep class **.R$drawable { *; }
-keep class **.R$anim { *; }
-keep class **.R$xml { *; }

# Prevent ritual configuration obfuscation
-keepclassmembers class com.bhax.app.BuildConfig {
    public static boolean DEBUG;
    public static String APPLICATION_ID;
    public static String BUILD_TYPE;
    public static String FLAVOR;
    public static int VERSION_CODE;
    public static String VERSION_NAME;
}
