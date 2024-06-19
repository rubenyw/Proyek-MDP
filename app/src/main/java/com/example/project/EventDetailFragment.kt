package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.Locale

class EventDetailFragment : Fragment() {

    lateinit var buttonBack: FloatingActionButton
    lateinit var imageViewEventImage: ImageView
    lateinit var tvTitle: TextView
    lateinit var tvLocation: TextView
    lateinit var tvSchedule: TextView
    lateinit var tvDescription: TextView
    lateinit var tvDonation: TextView
    private lateinit var eventId: String
    private lateinit var eventName: String
    private lateinit var eventLocation: String
    private lateinit var eventDate: String
    private lateinit var eventDescription: String
    private lateinit var eventImageUrl: String
    private lateinit var eventDonation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            eventId = it.getString("eventId") ?: ""
            eventName = it.getString("eventName") ?: ""
            eventLocation = it.getString("eventLocation") ?: ""
            eventDate = it.getString("eventDate") ?: ""
            eventDescription = it.getString("eventDescription") ?: ""
            eventImageUrl = it.getString("eventImageUrl") ?: ""
            eventDonation = it.getInt("eventDonation").toString() ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_event_detail, container, false)

        buttonBack = v.findViewById(R.id.floatingActionButtonBackEventDetail)
        imageViewEventImage = v.findViewById(R.id.imageViewEventDetailPage)
        tvTitle = v.findViewById(R.id.tvTitleEventDetailPage)
        tvDescription = v.findViewById(R.id.tvDescriptionEventDetailPage)
        tvDonation = v.findViewById(R.id.tvDonationEventDetail)
        tvLocation = v.findViewById(R.id.tvLocationEventDetail)
        tvSchedule = v.findViewById(R.id.tvDateTimeEventDetailPage)

        tvTitle.setText(eventName)
        tvDescription.setText(eventDescription)
        tvSchedule.setText(eventDate)
        tvLocation.setText(eventLocation)
        tvDonation.text = formatRupiah(eventDonation.toInt())

        Glide.with(this)
            .load(eventImageUrl)
            .into(imageViewEventImage)

        buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_global_upcomingEventsFragment)
        }

        return v
    }

    private fun formatRupiah(amount: Int): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(amount)
    }
}