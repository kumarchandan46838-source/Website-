package com.yesmin.ai.ui.memory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yesmin.ai.R
import com.yesmin.ai.data.db.MemoryEntity

class MemoryAdapter(
    private val memories: MutableList<MemoryEntity> = mutableListOf(),
    private val onDelete: (MemoryEntity) -> Unit
) : RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>() {

    fun submitList(newList: List<MemoryEntity>) {
        memories.clear()
        memories.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memory, parent, false)
        return MemoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        holder.bind(memories[position])
    }

    override fun getItemCount(): Int = memories.size

    inner class MemoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_memory_category)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_memory_title)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_memory_content)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete_memory)

        fun bind(memory: MemoryEntity) {
            tvCategory.text = memory.category
            tvTitle.text = memory.title
            tvContent.text = memory.content
            btnDelete.setOnClickListener { onDelete(memory) }
        }
    }
}
