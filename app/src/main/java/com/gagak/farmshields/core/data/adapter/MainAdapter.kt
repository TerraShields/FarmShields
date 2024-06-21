package com.gagak.farmshields.core.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gagak.farmshields.R
import com.gagak.farmshields.core.domain.model.main.ReportModel
import com.gagak.farmshields.core.utils.Date
import com.gagak.farmshields.core.utils.GeocodingUtils
import com.gagak.farmshields.ui.home.HomeFragmentDirections

class MainAdapter(private val context: android.content.Context) : PagingDataAdapter<ReportModel, MainAdapter.ReportViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = getItem(position)
        if (report != null) {
            holder.bind(report, context)
        }
    }

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageHama: ImageView = itemView.findViewById(R.id.image_hama)
        private val reportId: TextView = itemView.findViewById(R.id.report_id)
        private val hama: TextView = itemView.findViewById(R.id.hama)
        private val lokasi: TextView = itemView.findViewById(R.id.lokasi)
        private val tanggal: TextView = itemView.findViewById(R.id.tanggal)
        private val keterangan: TextView = itemView.findViewById(R.id.keterangan)
        private val sign: TextView = itemView.findViewById(R.id.sign)
        private val container: View = itemView.findViewById(R.id.report_container)

        fun bind(report: ReportModel, context: android.content.Context) {
            reportId.text = "ID Laporan: ${report.reportId}"
            hama.text = "Hama: ${report.classificationResult}"
            tanggal.text = "Tanggal: ${Date.formatTimeDifference(Date.parseIso8601(report.createdAt), System.currentTimeMillis())}"
            lokasi.text = "Lokasi: ${GeocodingUtils.getLocationName(context, report.location.latitude.toDouble(), report.location.longitude.toDouble())}"
            keterangan.text = "Keterangan: ${report.description}"
            sign.text = report.sign

            Glide.with(itemView.context)
                .load(report.image)
                .into(imageHama)

            when (report.sign.lowercase()) {
                "bahaya" -> {
                    sign.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.dangerSign))
                    container.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.dangerContainer))
                }
                "aman" -> {
                    sign.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.safeSign))
                    container.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.safeContainer))
                }
                "peringatan" -> {
                    sign.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.warningnSign))
                    container.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.warningContainer))
                }
            }

            itemView.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToHomeDetailsFragment(report)
                it.findNavController().navigate(action)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ReportModel>() {
            override fun areItemsTheSame(oldItem: ReportModel, newItem: ReportModel): Boolean {
                return oldItem.reportId == newItem.reportId
            }

            override fun areContentsTheSame(oldItem: ReportModel, newItem: ReportModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
