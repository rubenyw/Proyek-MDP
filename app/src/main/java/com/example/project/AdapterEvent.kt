package com.example.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class AdapterEvent(var context: Context, var arr: List<EventClass>)
    : RecyclerView.Adapter<AdapterEvent.DpHolder>() {

    class DpHolder(it: View) : RecyclerView.ViewHolder(it) {
        var txtName: TextView = it.findViewById(R.id.tvEventNameUpcomingPage)
        var txtLocation: TextView = it.findViewById(R.id.tvLocationUpcomingPage)
        var txtDate: TextView = it.findViewById(R.id.tvDateUpcomingPage)
        var txtDescription: TextView = it.findViewById(R.id.tvEventDescriptionUpcomingPage)
        var imageEvent: ImageView = it.findViewById(R.id.imageViewEventUpcomingPage)
        var mycons: ConstraintLayout = it.findViewById(R.id.eventItemConstraintLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DpHolder {
        var convertview = LayoutInflater.from(context).inflate(R.layout.rv_event_item, parent, false)
        return DpHolder(convertview)
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: DpHolder, position: Int) {
        holder.txtName.setText(arr[position].name.toString())
        holder.txtLocation.setText(arr[position].location.toString())
        holder.txtDate.setText(arr[position].date.toString())
        holder.txtDescription.setText(arr[position].description.toString())
        // Load image from URL using Glide
        Glide.with(context)
            .load(arr[position].urlLink)  // Ensure imageUrl is a property of EventClass
            .into(holder.imageEvent)
        holder.mycons.setOnClickListener(View.OnClickListener {
//            (context as MainActivity).openDetail(arr[position])
        })
    }

}