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
import androidx.recyclerview.widget.GridLayoutManager
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

        eventsData = vmEvent.getEvents();
        adapter = AdapterEvent(requireContext(), eventsData)
        rvEventsList.adapter = adapter
        var mlayout = GridLayoutManager(requireContext(), 1)
        rvEventsList.layoutManager = mlayout

        return v
    }
}