package com.yesmin.ai.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.yesmin.ai.R
import com.yesmin.ai.YesminApplication
import com.yesmin.ai.ui.MainActivity
import com.yesmin.ai.ui.automation.AutomationFragment
import com.yesmin.ai.ui.calling.CallingFragment
import com.yesmin.ai.ui.chat.ChatFragment
import com.yesmin.ai.ui.coding.CodingFragment
import com.yesmin.ai.ui.memory.MemoryFragment
import com.yesmin.ai.ui.privacy.PrivacyFragment
import com.yesmin.ai.ui.settings.SettingsFragment
import com.yesmin.ai.ui.tools.ToolsFragment

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val app = requireActivity().application as YesminApplication
        val prefs = app.preferencesManager

        val tvStatus = root.findViewById<TextView>(R.id.tv_brain_status)
        val hasKey = prefs.geminiApiKey.isNotBlank()
        tvStatus.text = if (hasKey) "Gemini Connected • ${prefs.selectedModel}" else "Configure API Key in Settings"

        root.findViewById<LinearLayout>(R.id.btn_hero_call).setOnClickListener {
            (activity as? MainActivity)?.navigateTo(CallingFragment())
        }

        root.findViewById<LinearLayout>(R.id.btn_tile_chat).setOnClickListener {
            (activity as? MainActivity)?.navigateTo(ChatFragment())
        }

        root.findViewById<LinearLayout>(R.id.btn_tile_coding).setOnClickListener {
            (activity as? MainActivity)?.navigateTo(CodingFragment())
        }

        root.findViewById<LinearLayout>(R.id.btn_tile_automation).setOnClickListener {
            (activity as? MainActivity)?.navigateTo(AutomationFragment())
        }

        root.findViewById<LinearLayout>(R.id.btn_tile_memory).setOnClickListener {
            (activity as? MainActivity)?.navigateTo(MemoryFragment())
        }

        root.findViewById<LinearLayout>(R.id.btn_tile_tools).setOnClickListener {
            (activity as? MainActivity)?.navigateTo(ToolsFragment())
        }

        root.findViewById<LinearLayout>(R.id.btn_tile_settings).setOnClickListener {
            (activity as? MainActivity)?.navigateTo(SettingsFragment())
        }

        root.findViewById<ImageView>(R.id.btn_privacy_center).setOnClickListener {
            (activity as? MainActivity)?.navigateTo(PrivacyFragment())
        }

        return root
    }
}
