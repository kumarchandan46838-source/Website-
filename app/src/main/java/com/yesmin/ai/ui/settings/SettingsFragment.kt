package com.yesmin.ai.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yesmin.ai.R
import com.yesmin.ai.YesminApplication
import com.yesmin.ai.service.gemini.GeminiApiClient
import com.yesmin.ai.ui.MainActivity
import com.yesmin.ai.ui.about.AboutFragment
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val app = requireActivity().application as YesminApplication
        val prefs = app.preferencesManager
        val geminiClient = GeminiApiClient()

        val etApiKey = root.findViewById<EditText>(R.id.et_api_key)
        val btnSaveApi = root.findViewById<Button>(R.id.btn_save_api)
        val btnTestApi = root.findViewById<Button>(R.id.btn_test_api)
        val tvStatus = root.findViewById<TextView>(R.id.tv_api_status)
        val spinnerModel = root.findViewById<Spinner>(R.id.spinner_model)
        val spinnerPersonality = root.findViewById<Spinner>(R.id.spinner_personality)
        val sbPitch = root.findViewById<SeekBar>(R.id.sb_voice_pitch)
        val sbSpeed = root.findViewById<SeekBar>(R.id.sb_voice_speed)

        etApiKey.setText(prefs.geminiApiKey)

        val models = arrayOf("gemini-1.5-flash", "gemini-2.0-flash", "gemini-1.5-pro")
        val modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, models)
        spinnerModel.adapter = modelAdapter
        val modelIndex = models.indexOf(prefs.selectedModel).coerceAtLeast(0)
        spinnerModel.setSelection(modelIndex)

        val personas = arrayOf("BALANCED", "GIRLFRIEND", "JARVIS")
        val personaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, personas)
        spinnerPersonality.adapter = personaAdapter
        val personaIndex = personas.indexOf(prefs.personalityMode).coerceAtLeast(0)
        spinnerPersonality.setSelection(personaIndex)

        sbPitch.progress = (prefs.voicePitch * 100).toInt()
        sbSpeed.progress = (prefs.speechRate * 100).toInt()

        btnSaveApi.setOnClickListener {
            val key = etApiKey.text.toString().trim()
            prefs.geminiApiKey = key
            prefs.selectedModel = spinnerModel.selectedItem.toString()
            prefs.personalityMode = spinnerPersonality.selectedItem.toString()
            prefs.voicePitch = sbPitch.progress / 100f
            prefs.speechRate = sbSpeed.progress / 100f
            app.voiceEngine.applyPreferences()
            Toast.makeText(requireContext(), "Settings saved securely!", Toast.LENGTH_SHORT).show()
        }

        btnTestApi.setOnClickListener {
            val key = etApiKey.text.toString().trim()
            if (key.isBlank()) {
                tvStatus.text = "Please enter an API key first."
                return@setOnClickListener
            }
            tvStatus.text = "Testing connection with Google Gemini..."
            lifecycleScope.launch {
                val res = geminiClient.testConnection(key, spinnerModel.selectedItem.toString())
                res.onSuccess { answer ->
                    tvStatus.text = "Connection Successful! Gemini says: '$answer'"
                }.onFailure { err ->
                    tvStatus.text = "Connection Failed: ${err.message}"
                }
            }
        }

        root.findViewById<Button>(R.id.btn_open_about).setOnClickListener {
            (activity as? MainActivity)?.navigateTo(AboutFragment())
        }

        return root
    }
}
