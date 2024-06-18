package com.example.project

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.findNavController

class CreateEventFragment : Fragment() {

    lateinit var backButton: Button
    lateinit var eventNameInput: EditText
    lateinit var eventDescriptionInput: EditText
    lateinit var eventDonationInput: EditText
    lateinit var eventLocationInput: EditText
    lateinit var eventDateTimeInput: EditText
    lateinit var eventImageButton: Button
    lateinit var eventImageName: TextView
    lateinit var createEventButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_create_event, container, false)

        backButton = v.findViewById(R.id.buttonBackCreateEvent)
        eventNameInput = v.findViewById(R.id.etEventName)
        eventDescriptionInput = v.findViewById(R.id.etEventDescription)
        eventDonationInput = v.findViewById(R.id.etDonationCreateEvent)
        eventLocationInput = v.findViewById(R.id.etLocationCreateEvent)
        eventDateTimeInput = v.findViewById(R.id.etDateTimeCreateEvent)
        eventImageButton = v.findViewById(R.id.buttonUploadImageCreateEventPage)
        eventImageName = v.findViewById(R.id.tvImageNameCreateEvent)
        createEventButton = v.findViewById(R.id.buttonCreateEventCreateEventPage)

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_upcomingEventsFragment)
        }

        return v
    }
}