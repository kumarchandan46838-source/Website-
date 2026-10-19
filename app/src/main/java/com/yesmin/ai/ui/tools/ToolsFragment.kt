package com.yesmin.ai.ui.tools

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yesmin.ai.R
import com.yesmin.ai.YesminApplication
import com.yesmin.ai.data.repository.YesminRepository
import com.yesmin.ai.service.gemini.GeminiApiClient
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ToolsFragment : Fragment() {

    private lateinit var repository: YesminRepository
    private var selectedBase64: String? = null
    private lateinit var ivImage: ImageView
    private lateinit var tvResult: TextView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                ivImage.setImageBitmap(bitmap)
                ivImage.visibility = View.VISIBLE

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                selectedBase64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_tools, container, false)
        val app = requireActivity().application as YesminApplication
        repository = YesminRepository(app.database, app.preferencesManager, GeminiApiClient())

        ivImage = root.findViewById(R.id.iv_selected_image)
        tvResult = root.findViewById(R.id.tv_vision_result)
        val btnPick = root.findViewById<Button>(R.id.btn_pick_image)
        val btnAnalyze = root.findViewById<Button>(R.id.btn_analyze_image)

        btnPick.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnAnalyze.setOnClickListener {
            if (selectedBase64 == null) {
                Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            tvResult.visibility = View.VISIBLE
            tvResult.text = "Yesmin is inspecting your image..."

            lifecycleScope.launch {
                val prompt = "Describe this image in detail, extract any text visible, and tell me your thoughts warmly in Hinglish or English."
                val result = repository.generateAiResponse(prompt, selectedBase64)
                result.onSuccess { analysis ->
                    tvResult.text = analysis
                }.onFailure { err ->
                    tvResult.text = "Error analyzing image: ${err.message}"
                }
            }
        }

        return root
    }
}
