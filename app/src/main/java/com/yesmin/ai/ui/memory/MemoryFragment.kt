package com.yesmin.ai.ui.memory

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yesmin.ai.R
import com.yesmin.ai.YesminApplication
import com.yesmin.ai.data.db.MemoryEntity
import com.yesmin.ai.data.repository.YesminRepository
import com.yesmin.ai.service.gemini.GeminiApiClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MemoryFragment : Fragment() {

    private lateinit var adapter: MemoryAdapter
    private lateinit var repository: YesminRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_memory, container, false)
        val app = requireActivity().application as YesminApplication
        repository = YesminRepository(app.database, app.preferencesManager, GeminiApiClient())

        val rv = root.findViewById<RecyclerView>(R.id.rv_memories)
        val tvEmpty = root.findViewById<TextView>(R.id.tv_empty_memory)
        val btnAdd = root.findViewById<Button>(R.id.btn_add_memory)
        val btnClear = root.findViewById<Button>(R.id.btn_wipe_memories)

        adapter = MemoryAdapter { memory ->
            lifecycleScope.launch { repository.deleteMemory(memory) }
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        lifecycleScope.launch {
            repository.allMemories.collectLatest { list ->
                adapter.submitList(list)
                tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        btnAdd.setOnClickListener {
            showAddMemoryDialog()
        }

        btnClear.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Wipe All Memories?")
                .setMessage("Are you sure? Yesmin will forget saved preferences and facts.")
                .setPositiveButton("Wipe All") { _, _ ->
                    lifecycleScope.launch { repository.clearMemories() }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        return root
    }

    private fun showAddMemoryDialog() {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.item_memory, null)
        val etTitle = EditText(requireContext()).apply { hint = "Title (e.g. Favorite Food)" }
        val etContent = EditText(requireContext()).apply { hint = "Detail (e.g. Loves Biryani & Chhole Bhature)" }

        val container = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(etTitle)
            addView(etContent)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Memory")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val title = etTitle.text.toString().trim()
                val content = etContent.text.toString().trim()
                if (title.isNotBlank() && content.isNotBlank()) {
                    lifecycleScope.launch {
                        repository.addMemory("PREFERENCE", title, content)
                        Toast.makeText(requireContext(), "Memory saved!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
