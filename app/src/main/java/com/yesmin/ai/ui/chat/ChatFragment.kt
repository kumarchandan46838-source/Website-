package com.yesmin.ai.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yesmin.ai.R
import com.yesmin.ai.YesminApplication
import com.yesmin.ai.data.repository.YesminRepository
import com.yesmin.ai.service.automation.AutomationManager
import com.yesmin.ai.service.gemini.GeminiApiClient
import com.yesmin.ai.service.voice.SpeechRecognizerHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private lateinit var adapter: ChatAdapter
    private lateinit var repository: YesminRepository
    private lateinit var automationManager: AutomationManager
    private var speechHelper: SpeechRecognizerHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_chat, container, false)
        val app = requireActivity().application as YesminApplication
        repository = YesminRepository(app.database, app.preferencesManager, GeminiApiClient())
        automationManager = AutomationManager(requireContext())

        val rv = root.findViewById<RecyclerView>(R.id.rv_messages)
        val etInput = root.findViewById<EditText>(R.id.et_message_input)
        val btnSend = root.findViewById<ImageButton>(R.id.btn_send)
        val btnMic = root.findViewById<ImageButton>(R.id.btn_mic)
        val btnClear = root.findViewById<ImageView>(R.id.btn_clear_chat)
        val tvTyping = root.findViewById<TextView>(R.id.tv_typing_indicator)

        adapter = ChatAdapter { text ->
            app.voiceEngine.speak(text)
        }
        rv.layoutManager = LinearLayoutManager(requireContext()).apply { stackFromEnd = true }
        rv.adapter = adapter

        lifecycleScope.launch {
            repository.allMessages.collectLatest { list ->
                adapter.submitList(list)
                if (list.isNotEmpty()) rv.scrollToPosition(list.size - 1)
            }
        }

        fun sendMessage(text: String) {
            val query = text.trim()
            if (query.isBlank()) return
            etInput.setText("")

            lifecycleScope.launch {
                repository.saveUserMessage(query)

                // Check for phone automation intent
                val autoResult = automationManager.tryParseAndExecute(query)
                if (autoResult is AutomationManager.AutomationResult.Success) {
                    val reply = "Done! ${autoResult.message}"
                    repository.saveAiMessage(reply)
                    app.voiceEngine.speak(reply)
                    return@launch
                }

                tvTyping.visibility = View.VISIBLE
                val result = repository.generateAiResponse(query)
                tvTyping.visibility = View.GONE

                result.onSuccess { reply ->
                    repository.saveAiMessage(reply)
                }.onFailure { err ->
                    val errorMsg = "Oops: ${err.message}"
                    repository.saveAiMessage(errorMsg)
                }
            }
        }

        btnSend.setOnClickListener {
            sendMessage(etInput.text.toString())
        }

        speechHelper = SpeechRecognizerHelper(requireContext()).apply {
            onResult = { spokenText ->
                sendMessage(spokenText)
            }
            onError = { err ->
                Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show()
            }
        }

        btnMic.setOnClickListener {
            speechHelper?.startListening()
            Toast.makeText(requireContext(), "Listening...", Toast.LENGTH_SHORT).show()
        }

        btnClear.setOnClickListener {
            lifecycleScope.launch { repository.clearChat() }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechHelper?.destroy()
    }
}
