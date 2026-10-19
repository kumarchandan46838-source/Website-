package com.yesmin.ai

import android.app.Application
import com.yesmin.ai.data.db.AppDatabase
import com.yesmin.ai.data.prefs.PreferencesManager
import com.yesmin.ai.service.voice.VoiceEngine

class YesminApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var voiceEngine: VoiceEngine
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        preferencesManager = PreferencesManager(this)
        voiceEngine = VoiceEngine(this, preferencesManager)
    }

    override fun onTerminate() {
        super.onTerminate()
        voiceEngine.shutdown()
    }

    companion object {
        lateinit var instance: YesminApplication
            private set
    }
}
