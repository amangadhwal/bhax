# Ritual effect classes
-keep class com.bhax.app.ui.ritual.** { *; }
-keep class com.bhax.app.util.** { *; }
-keepclassmembers class com.bhax.app.ui.ritual.** { *; }

# OpenGL related classes
-keep class android.opengl.** { *; }
-keep class javax.microedition.khronos.** { *; }
-keepclassmembers class * {
    *** onSurfaceCreated(...);
    *** onSurfaceChanged(...);
    *** onDrawFrame(...);
    *** createProgram(...);
    *** loadShader(...);
}

# Video playback
-keep class android.media.** { *; }
-keep class android.view.TextureView { *; }
-keep class android.view.Surface { *; }
-keep class android.graphics.SurfaceTexture { *; }

# Animation and effects
-keepclassmembers class * extends android.animation.ValueAnimator$AnimatorUpdateListener { *; }
-keepclassmembers class * extends android.animation.Animator$AnimatorListener { *; }
-keep class android.animation.** { *; }
-keep class android.view.animation.** { *; }

# Resource files
-keep class **.R
-keep class **.R$* {
    <fields>;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    *** get*();
}

# Keep shader code
-keepclassmembers class * {
    public void compileShader(int, java.lang.String);
    public void linkProgram(int);
    public void validateProgram(int);
}

# Lifecycle components
-keep class androidx.lifecycle.** { *; }
-keep class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}
-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <methods>;
}

# Native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Parcelables
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Serializable
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Debug logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep ritual shader programs
-keep class com.bhax.app.ui.ritual.RitualShader {
    private static final java.lang.String VERTEX_SHADER;
    private static final java.lang.String FRAGMENT_SHADER;
}
