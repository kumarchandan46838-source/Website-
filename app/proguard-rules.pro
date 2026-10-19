# Proguard rules for Yesmin AI
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
-keep class com.yesmin.ai.data.model.** { *; }
-keep class com.yesmin.ai.data.db.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
