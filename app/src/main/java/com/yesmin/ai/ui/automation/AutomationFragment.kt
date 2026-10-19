package com.yesmin.ai.ui.automation

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yesmin.ai.R
import com.yesmin.ai.service.automation.AutomationManager

class AutomationFragment : Fragment() {

    private lateinit var automationManager: AutomationManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_automation, container, false)
        automationManager = AutomationManager(requireContext())

        val etCmd = root.findViewById<EditText>(R.id.et_automation_cmd)
        val btnRun = root.findViewById<Button>(R.id.btn_run_automation)
        val btnToggleAccess = root.findViewById<Button>(R.id.btn_toggle_accessibility)
        val btnToggleNotif = root.findViewById<Button>(R.id.btn_toggle_notifications)

        btnRun.setOnClickListener {
            val cmd = etCmd.text.toString().trim()
            if (cmd.isNotBlank()) {
                val result = automationManager.tryParseAndExecute(cmd)
                when (result) {
                    is AutomationManager.AutomationResult.Success -> Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    is AutomationManager.AutomationResult.Failure -> Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    AutomationManager.AutomationResult.NotAnAction -> Toast.makeText(requireContext(), "Not recognized as a device action.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnToggleAccess.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        btnToggleNotif.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        root.findViewById<Button>(R.id.btn_quick_yt_music).setOnClickListener {
            automationManager.playYouTube("trending bollywood hindi songs")
        }

        root.findViewById<Button>(R.id.btn_quick_alarm).setOnClickListener {
            automationManager.setAlarm("7:00 am")
        }

        root.findViewById<Button>(R.id.btn_quick_settings).setOnClickListener {
            automationManager.openSettings()
        }

        return root
    }
}
