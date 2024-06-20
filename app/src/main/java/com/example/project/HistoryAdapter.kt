package com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(private val donations: List<DonationHistoryEntity>) :
    RecyclerView.Adapter<HistoryAdapter.DonationHistoryEntityViewHolder>() {

    class DonationHistoryEntityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountTextView: TextView = itemView.findViewById(R.id.item_ammount_tv)
        val messageTextView: TextView = itemView.findViewById(R.id.item_message_tv)
        val timeTextView: TextView = itemView.findViewById(R.id.item_time_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationHistoryEntityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.donation_history_item, parent, false)
        return DonationHistoryEntityViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationHistoryEntityViewHolder, position: Int) {
        val donation = donations[position]
        holder.amountTextView.setText(Mekanisme.formatToRupiah(donation.ammount.toInt()));
        holder.messageTextView.setText(donation.message);
        holder.timeTextView.setText(formatDate(donation.time.toDate()));
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) // Adjust the date format as needed
        return format.format(date)
    }
    override fun getItemCount(): Int = donations.size
}
