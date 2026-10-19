# Yesmin AI — Female AI Companion & JARVIS Android Assistant

**Yesmin AI** is a native Android application combining an affectionate female AI companion and a powerful JARVIS-style phone assistant. Powered by Google Gemini AI, Android SpeechRecognizer, TextToSpeech, Room Database, and permission-based device automation.

---

## 🌟 Key Features

1. **AI Brain Powered by Google Gemini**:
   - Supports `gemini-1.5-flash`, `gemini-2.0-flash`, and `gemini-1.5-pro`.
   - Encrypted API key storage with password masking.
   - Built-in "Test Connection" diagnosis.
   - Multilingual support: Hindi, Hinglish, and English.

2. **Real-Time Voice Calling Mode**:
   - Dedicated full-screen calling experience.
   - Live two-way continuous audio conversation.
   - Animated holographic soundwave visualizer (`PulsingAvatarView`) reflecting AI states (Listening, Thinking, Speaking).
   - Real-time subtitles, Mute, Interrupt/Barge-in, and auto-return to listening.

3. **Phone Automation**:
   - Launch installed apps by voice command.
   - Search and play music on YouTube.
   - Set alarms, timers, and calendar reminders.
   - Hands-free phone calling and messaging intents.
   - Optional Accessibility Service for authorized screen navigation and reading.

4. **Coding Studio**:
   - Code generation across Kotlin, Java, Python, HTML/JS.
   - Syntax-highlighted monospaced display.
   - One-tap copy to clipboard and file export.
   - Built-in Termux APK compilation guides.

5. **Persistent Memory Bank**:
   - Stores user preferences, favorites, and facts offline in local Room SQLite database.
   - Memories are dynamically injected into the Gemini prompt so Yesmin never forgets you.
   - Full memory viewer, search, edit, delete, and wipe-all capabilities.

6. **Vision & AI Tools**:
   - Camera & Gallery image analysis via Gemini Vision multimodal API.
   - OCR and text extraction.

7. **Futuristic Cyberpunk Theme**:
   - High-contrast dark void palette (`#0B0D19`).
   - Neon purple, cyan, and subtle pink accents.
   - Lightweight and optimized for phones with 4 GB RAM.

---

## 📁 Project Structure

```
yesmin_ai_project/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/yesmin/ai/
│       │   ├── YesminApplication.kt
│       │   ├── data/
│       │   │   ├── db/ (Room DB: MemoryEntity, ChatMessageEntity, DAOs)
│       │   │   ├── model/ (GeminiRequest, GeminiResponse)
│       │   │   ├── prefs/ (PreferencesManager - EncryptedSharedPreferences)
│       │   │   └── repository/ (YesminRepository)
│       │   ├── service/
│       │   │   ├── accessibility/ (YesminAccessibilityService)
│       │   │   ├── automation/ (AutomationManager)
│       │   │   ├── gemini/ (GeminiApiClient, PersonalityPromptBuilder)
│       │   │   ├── notification/ (YesminNotificationListener)
│       │   │   └── voice/ (VoiceEngine, SpeechRecognizerHelper)
│       │   └── ui/
│       │       ├── MainActivity.kt
│       │       ├── views/ (PulsingAvatarView)
│       │       ├── home/ (HomeFragment)
│       │       ├── chat/ (ChatFragment, ChatAdapter)
│       │       ├── calling/ (CallingFragment)
│       │       ├── coding/ (CodingFragment)
│       │       ├── automation/ (AutomationFragment)
│       │       ├── memory/ (MemoryFragment, MemoryAdapter)
│       │       ├── tools/ (ToolsFragment)
│       │       ├── settings/ (SettingsFragment)
│       │       ├── privacy/ (PrivacyFragment)
│       │       └── about/ (AboutFragment)
│       └── res/ (Layouts, Drawables, Values, Colors, Themes)
├── gradle/wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── termux-build.sh
├── README.md
└── TERMUX_GUIDE.md
```

---

## 🛠️ How to Build APK in Termux on Android Phone

See `TERMUX_GUIDE.md` for a complete step-by-step tutorial.
Quick command:
```bash
bash termux-build.sh
```
