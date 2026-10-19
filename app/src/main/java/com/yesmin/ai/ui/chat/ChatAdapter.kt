package com.yesmin.ai.ui.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yesmin.ai.R
import com.yesmin.ai.data.db.ChatMessageEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
    private val messages: MutableList<ChatMessageEntity> = mutableListOf(),
    private val onSpeakClicked: (String) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun submitList(newMessages: List<ChatMessageEntity>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rootLayout: LinearLayout = itemView.findViewById(R.id.layout_bubble_root)
        private val container: LinearLayout = itemView.findViewById(R.id.layout_bubble_container)
        private val tvSender: TextView = itemView.findViewById(R.id.tv_sender_title)
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message_text)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_timestamp)
        private val btnCopy: ImageView = itemView.findViewById(R.id.btn_copy_text)
        private val btnSpeak: ImageView = itemView.findViewById(R.id.btn_tts_speak)

        fun bind(msg: ChatMessageEntity) {
            val isUser = msg.sender == "USER"
            tvSender.text = if (isUser) "You" else "Yesmin"
            tvSender.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (isUser) R.color.neon_pink_soft else R.color.neon_cyan
                )
            )

            tvMessage.text = msg.text
            tvTime.text = timeFormat.format(Date(msg.timestamp))

            container.setBackgroundResource(
                if (isUser) R.drawable.bg_chat_bubble_user else R.drawable.bg_chat_bubble_ai
            )

            rootLayout.gravity = if (isUser) android.view.Gravity.END else android.view.Gravity.START
            btnSpeak.visibility = if (isUser) View.GONE else View.VISIBLE

            btnCopy.setOnClickListener {
                val clipboard = itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Yesmin Message", msg.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(itemView.context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }

            btnSpeak.setOnClickListener {
                onSpeakClicked(msg.text)
            }
        }
    }
}
