package com.yesmin.ai.ui.calling

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yesmin.ai.R
import com.yesmin.ai.YesminApplication
import com.yesmin.ai.data.repository.YesminRepository
import com.yesmin.ai.service.automation.AutomationManager
import com.yesmin.ai.service.gemini.GeminiApiClient
import com.yesmin.ai.service.voice.SpeechRecognizerHelper
import com.yesmin.ai.ui.views.PulsingAvatarView
import kotlinx.coroutines.launch

class CallingFragment : Fragment() {

    private lateinit var avatarView: PulsingAvatarView
    private lateinit var tvState: TextView
    private lateinit var tvSubtitles: TextView
    private lateinit var tvDuration: TextView
    private lateinit var repository: YesminRepository
    private lateinit var automationManager: AutomationManager
    private var speechHelper: SpeechRecognizerHelper? = null

    private var isCallActive = false
    private var isMuted = false
    private var callSeconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isCallActive) {
                callSeconds++
                val m = callSeconds / 60
                val s = callSeconds % 60
                tvDuration.text = "%02d:%02d".format(m, s)
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_calling, container, false)
        val app = requireActivity().application as YesminApplication
        repository = YesminRepository(app.database, app.preferencesManager, GeminiApiClient())
        automationManager = AutomationManager(requireContext())

        avatarView = root.findViewById(R.id.pulsing_avatar)
        tvState = root.findViewById(R.id.tv_calling_state_label)
        tvSubtitles = root.findViewById(R.id.tv_call_subtitles)
        tvDuration = root.findViewById(R.id.tv_call_duration)

        val btnMute = root.findViewById<ImageButton>(R.id.btn_call_mute)
        val btnInterrupt = root.findViewById<ImageButton>(R.id.btn_call_interrupt)
        val btnEnd = root.findViewById<ImageButton>(R.id.btn_call_end)

        setupSpeechRecognizer()
        setupTtsCallbacks()

        btnMute.setOnClickListener {
            isMuted = !isMuted
            btnMute.setColorFilter(if (isMuted) ContextCompat.getColor(requireContext(), R.color.color_error) else ContextCompat.getColor(requireContext(), R.color.text_primary))
            Toast.makeText(requireContext(), if (isMuted) "Call Muted" else "Call Unmuted", Toast.LENGTH_SHORT).show()
        }

        btnInterrupt.setOnClickListener {
            app.voiceEngine.stop()
            setAvatarState(PulsingAvatarView.State.LISTENING, "Interrupted! Listening...")
            startListening()
        }

        btnEnd.setOnClickListener {
            endCall()
            parentFragmentManager.popBackStack()
        }

        startCall()
        return root
    }

    private fun setupSpeechRecognizer() {
        speechHelper = SpeechRecognizerHelper(requireContext()).apply {
            onResult = { spokenText ->
                if (!isCallActive || isMuted) return@SpeechRecognizerHelper
                tvSubtitles.text = "You: $spokenText"
                handleUserSpeech(spokenText)
            }
            onError = {
                if (isCallActive && !isMuted && !requireActivity().isFinishing) {
                    // Automatically restart listening on timeout or silence
                    handler.postDelayed({ startListening() }, 1000)
                }
            }
            onListeningStateChanged = { listening ->
                if (listening) {
                    setAvatarState(PulsingAvatarView.State.LISTENING, "Listening to you...")
                }
            }
        }
    }

    private fun setupTtsCallbacks() {
        val app = requireActivity().application as YesminApplication
        app.voiceEngine.onSpeechStateChanged = { speaking ->
            activity?.runOnUiThread {
                if (speaking) {
                    setAvatarState(PulsingAvatarView.State.SPEAKING, "Yesmin is speaking...")
                } else if (isCallActive) {
                    // Speech finished: automatically return to listening!
                    setAvatarState(PulsingAvatarView.State.LISTENING, "Listening to you...")
                    startListening()
                }
            }
        }
    }

    private fun startCall() {
        isCallActive = true
        callSeconds = 0
        handler.post(timerRunnable)
        val app = requireActivity().application as YesminApplication
        val greeting = "Hey there! I am listening. How can I brighten your day?"
        tvSubtitles.text = "Yesmin: $greeting"
        setAvatarState(PulsingAvatarView.State.SPEAKING, "Yesmin is greeting you...")
        app.voiceEngine.speak(greeting)
    }

    private fun handleUserSpeech(text: String) {
        val app = requireActivity().application as YesminApplication
        setAvatarState(PulsingAvatarView.State.THINKING, "Yesmin is thinking...")

        lifecycleScope.launch {
            repository.saveUserMessage(text)

            // Check automation
            val autoResult = automationManager.tryParseAndExecute(text)
            if (autoResult is AutomationManager.AutomationResult.Success) {
                val reply = "Right away! ${autoResult.message}"
                tvSubtitles.text = "Yesmin: $reply"
                repository.saveAiMessage(reply)
                app.voiceEngine.speak(reply)
                return@launch
            }

            val result = repository.generateAiResponse(text)
            result.onSuccess { reply ->
                tvSubtitles.text = "Yesmin: $reply"
                repository.saveAiMessage(reply)
                app.voiceEngine.speak(reply)
            }.onFailure { err ->
                val errorMsg = "Sorry, I had trouble thinking: ${err.message}"
                tvSubtitles.text = "Yesmin: $errorMsg"
                app.voiceEngine.speak(errorMsg)
            }
        }
    }

    private fun startListening() {
        if (!isCallActive || isMuted) return
        speechHelper?.startListening()
    }

    private fun setAvatarState(state: PulsingAvatarView.State, label: String) {
        avatarView.setState(state)
        tvState.text = label
    }

    private fun endCall() {
        isCallActive = false
        handler.removeCallbacks(timerRunnable)
        val app = requireActivity().application as YesminApplication
        app.voiceEngine.stop()
        speechHelper?.stopListening()
        setAvatarState(PulsingAvatarView.State.IDLE, "Call Ended")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        endCall()
        speechHelper?.destroy()
    }
}
