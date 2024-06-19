package com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val donations: List<DonationHistoryEntity>) :
    RecyclerView.Adapter<HistoryAdapter.DonationHistoryEntityViewHolder>() {

    class DonationHistoryEntityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountTextView: TextView = itemView.findViewById(R.id.item_ammount_tv)
        val messageTextView: TextView = itemView.findViewById(R.id.item_message_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationHistoryEntityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.donation_history_item, parent, false)
        return DonationHistoryEntityViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationHistoryEntityViewHolder, position: Int) {
        val donation = donations[position]
        holder.amountTextView.setText(donation.amount);
        holder.messageTextView.setText(donation.message)
    }

    override fun getItemCount(): Int = donations.size
}
