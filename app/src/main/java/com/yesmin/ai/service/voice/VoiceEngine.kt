package com.yesmin.ai.service.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.yesmin.ai.data.prefs.PreferencesManager
import java.util.Locale

class VoiceEngine(
    private val context: Context,
    private val prefs: PreferencesManager
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    var onSpeechStateChanged: ((Boolean) -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("hi", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
            applyPreferences()
            setupListener()
            isInitialized = true
        } else {
            Log.e("VoiceEngine", "TextToSpeech initialization failed.")
        }
    }

    fun applyPreferences() {
        tts?.setPitch(prefs.voicePitch)
        tts?.setSpeechRate(prefs.speechRate)
    }

    private fun setupListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                onSpeechStateChanged?.invoke(true)
            }

            override fun onDone(utteranceId: String?) {
                onSpeechStateChanged?.invoke(false)
            }

            override fun onError(utteranceId: String?) {
                onSpeechStateChanged?.invoke(false)
            }
        })
    }

    fun speak(text: String, utteranceId: String = "yesmin_speech") {
        if (!isInitialized) return
        stop()
        applyPreferences()
        // Clean markdown syntax for natural spoken output
        val cleanSpeech = text
            .replace(Regex("```[\s\S]*?```"), "Code snippet displayed on screen.")
            .replace(Regex("[*#_`]"), "")
            .trim()

        tts?.speak(cleanSpeech, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
            onSpeechStateChanged?.invoke(false)
        }
    }

    fun isSpeaking(): Boolean = tts?.isSpeaking == true

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
