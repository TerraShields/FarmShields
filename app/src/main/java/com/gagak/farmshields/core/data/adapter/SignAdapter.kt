package com.gagak.farmshields.core.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SignAdapter(private val signs: List<String>, private val itemClickListener: (String) -> Unit) : RecyclerView.Adapter<SignAdapter.SignViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return SignViewHolder(view)
    }

    override fun onBindViewHolder(holder: SignViewHolder, position: Int) {
        val sign = signs[position]
        holder.bind(sign, itemClickListener)
    }

    override fun getItemCount(): Int = signs.size

    class SignViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(sign: String, itemClickListener: (String) -> Unit) {
            textView.text = sign
            itemView.setOnClickListener { itemClickListener(sign) }
        }
    }
}
