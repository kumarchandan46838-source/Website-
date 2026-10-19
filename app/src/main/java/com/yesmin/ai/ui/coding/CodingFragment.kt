package com.yesmin.ai.ui.coding

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.ChipGroup
import com.yesmin.ai.R
import com.yesmin.ai.YesminApplication
import com.yesmin.ai.data.repository.YesminRepository
import com.yesmin.ai.service.gemini.GeminiApiClient
import kotlinx.coroutines.launch
import java.io.File

class CodingFragment : Fragment() {

    private lateinit var repository: YesminRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_coding, container, false)
        val app = requireActivity().application as YesminApplication
        repository = YesminRepository(app.database, app.preferencesManager, GeminiApiClient())

        val etPrompt = root.findViewById<EditText>(R.id.et_code_prompt)
        val chipGroup = root.findViewById<ChipGroup>(R.id.chip_group_lang)
        val btnGenerate = root.findViewById<Button>(R.id.btn_generate_code)
        val tvOutput = root.findViewById<TextView>(R.id.tv_code_output)
        val btnCopy = root.findViewById<Button>(R.id.btn_copy_code)
        val btnExport = root.findViewById<Button>(R.id.btn_export_code)
        val btnTermux = root.findViewById<Button>(R.id.btn_open_termux_guide)

        btnGenerate.setOnClickListener {
            val prompt = etPrompt.text.toString().trim()
            if (prompt.isBlank()) {
                Toast.makeText(requireContext(), "Please enter coding requirements", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val lang = when (chipGroup.checkedChipId) {
                R.id.chip_python -> "Python"
                R.id.chip_java -> "Java"
                R.id.chip_web -> "HTML/JS"
                else -> "Kotlin / Android"
            }

            val fullPrompt = "Act as an expert software architect. Language: $lang. Request: $prompt. Provide clean, production-ready code with exact file paths and clear explanations."
            tvOutput.text = "// Yesmin is crafting your code..."

            lifecycleScope.launch {
                val result = repository.generateAiResponse(fullPrompt)
                result.onSuccess { code ->
                    tvOutput.text = code
                }.onFailure { err ->
                    tvOutput.text = "// Error generating code: ${err.message}"
                }
            }
        }

        btnCopy.setOnClickListener {
            val text = tvOutput.text.toString()
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Yesmin Code", text))
            Toast.makeText(requireContext(), "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
        }

        btnExport.setOnClickListener {
            try {
                val exportDir = File(requireContext().getExternalFilesDir(null), "exports")
                exportDir.mkdirs()
                val file = File(exportDir, "yesmin_code_export_${System.currentTimeMillis()}.txt")
                file.writeText(tvOutput.text.toString())
                Toast.makeText(requireContext(), "Saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        btnTermux.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Termux APK Build Quick-Commands")
                .setMessage("1. pkg update && pkg install openjdk-17 gradle git\n2. cd yesmin_ai_project\n3. chmod +x gradlew\n4. ./gradlew assembleDebug\n5. APK located at: app/build/outputs/apk/debug/app-debug.apk\n6. termux-open app/build/outputs/apk/debug/app-debug.apk")
                .setPositiveButton("Got it", null)
                .show()
        }

        return root
    }
}
