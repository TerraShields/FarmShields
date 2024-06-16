package com.gagak.farmshields.core.domain.adapter.anita

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gagak.farmshields.core.domain.model.viewmodel.anita.ChatMessage
import com.gagak.farmshields.databinding.AnitaLayoutBinding
import com.gagak.farmshields.databinding.AnitaUserLayoutBinding

class AnitaChatAdapter(private val chatList: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_SYSTEM = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_SYSTEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val binding = AnitaUserLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            UserViewHolder(binding)
        } else {
            val binding = AnitaLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SystemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = chatList[position]
        if (holder is UserViewHolder) {
            holder.bind(message.message)
        } else if (holder is SystemViewHolder) {
            holder.bind(message.message)
        }
    }

    override fun getItemCount(): Int = chatList.size

    class UserViewHolder(private val binding: AnitaUserLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: String) {
            binding.messageTextView.text = message
            binding.timestampTextView.text = "Timestamp" // Replace with actual timestamp logic
        }
    }

    class SystemViewHolder(private val binding: AnitaLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: String) {
            binding.messageTextView.text = message
            binding.timestampTextView.text = "Timestamp" // Replace with actual timestamp logic
        }
    }
}
