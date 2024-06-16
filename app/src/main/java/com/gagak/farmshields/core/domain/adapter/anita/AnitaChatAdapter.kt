package com.gagak.farmshields.core.domain.adapter.anita

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gagak.farmshields.databinding.AnitaLayoutBinding

class AnitaChatAdapter(private val chatList: List<String>) :
    RecyclerView.Adapter<AnitaChatAdapter.AnitaViewHolder>() {

    class AnitaViewHolder(private val binding: AnitaLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: String) {
            binding.messageTextView.text = message
            // Assuming the timestamp and avatar are static or generated within this method
            binding.timestampTextView.text = "Timestamp" // Replace with actual timestamp logic
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnitaViewHolder {
        val binding = AnitaLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnitaViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size
}
