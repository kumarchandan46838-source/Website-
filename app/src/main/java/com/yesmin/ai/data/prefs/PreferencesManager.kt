package com.yesmin.ai.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = try {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "yesmin_secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback to standard private preferences if security crypto fails on older test builds
        context.getSharedPreferences("yesmin_fallback_prefs", Context.MODE_PRIVATE)
    }

    var geminiApiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value.trim()).apply()

    var selectedModel: String
        get() = prefs.getString(KEY_MODEL, "gemini-1.5-flash") ?: "gemini-1.5-flash"
        set(value) = prefs.edit().putString(KEY_MODEL, value).apply()

    var personalityMode: String
        get() = prefs.getString(KEY_PERSONALITY, "BALANCED") ?: "BALANCED" // "GIRLFRIEND", "JARVIS", "BALANCED"
        set(value) = prefs.edit().putString(KEY_PERSONALITY, value).apply()

    var voicePitch: Float
        get() = prefs.getFloat(KEY_VOICE_PITCH, 1.25f) // Warm slightly higher pitch for female voice
        set(value) = prefs.edit().putFloat(KEY_VOICE_PITCH, value).apply()

    var speechRate: Float
        get() = prefs.getFloat(KEY_SPEECH_RATE, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_SPEECH_RATE, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "My Love") ?: "My Love"
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var isLiveVoiceEnabled: Boolean
        get() = prefs.getBoolean(KEY_LIVE_VOICE, true)
        set(value) = prefs.edit().putBoolean(KEY_LIVE_VOICE, value).apply()

    companion object {
        private const val KEY_API_KEY = "key_gemini_api_key"
        private const val KEY_MODEL = "key_model"
        private const val KEY_PERSONALITY = "key_personality"
        private const val KEY_VOICE_PITCH = "key_voice_pitch"
        private const val KEY_SPEECH_RATE = "key_speech_rate"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_LIVE_VOICE = "key_live_voice"
    }
}
