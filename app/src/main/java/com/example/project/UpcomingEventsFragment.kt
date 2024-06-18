package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UpcomingEventsFragment : Fragment() {
    lateinit var rvEventsList: RecyclerView
    lateinit var tvUpcomingEventsTitle: TextView
    lateinit var tvLabelUpcomingEvents: TextView
    lateinit var buttonNavToCreateEvent: Button
    lateinit var adapter: AdapterEvent
    var eventsData = ArrayList<EventClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_upcoming_events, container, false)

        val vmEvent : ViewModelEvent by viewModels()

        rvEventsList = v.findViewById(R.id.rvEventsList)
        tvUpcomingEventsTitle = v.findViewById(R.id.tvUpcomingEventsTitle)
        tvLabelUpcomingEvents = v.findViewById(R.id.tvLabelUpcomingEvents)
        buttonNavToCreateEvent = v.findViewById(R.id.buttonNavToCreateEvent)

        buttonNavToCreateEvent.setOnClickListener {
            Mekanisme.showToast(requireContext(), "KLIK");
            findNavController().navigate(R.id.action_global_createEventFragment)
        }

        eventsData = vmEvent.getEvents();
        eventsData.add(
            EventClass(
                "12342",
                "Lama Seklai",
                "12-05-2034",
                "Surabaya",
                "Halo-halo sruabaya tercinta"
            )
        )
        rvEventsList.adapter = AdapterEvent(requireContext(), eventsData)
        rvEventsList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        return v
    }
}