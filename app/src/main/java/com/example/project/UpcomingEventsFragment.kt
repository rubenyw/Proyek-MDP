package com.example.project

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UpcomingEventsFragment : Fragment() {

    lateinit var rvEventsList: RecyclerView
    lateinit var tvUpcomingEventsTitle: TextView
    lateinit var tvLabelUpcomingEvents: TextView
    lateinit var buttonNavToCreateEvent: Button
    lateinit var buttonNavToBackUpcomingEventPage: Button
    lateinit var adapter: AdapterEvent
    private var eventsData = ArrayList<EventClassUI>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_upcoming_events, container, false)

        val vmEvent: ViewModelEvent by viewModels()

        rvEventsList = v.findViewById(R.id.rvEventsList)
        tvUpcomingEventsTitle = v.findViewById(R.id.tvUpcomingEventsTitle)
        tvLabelUpcomingEvents = v.findViewById(R.id.tvLabelUpcomingEvents)
        buttonNavToCreateEvent = v.findViewById(R.id.buttonNavToCreateEvent)
        buttonNavToBackUpcomingEventPage = v.findViewById(R.id.buttonBackUpcomingEventPage)

        buttonNavToCreateEvent.setOnClickListener {
            findNavController().navigate(R.id.action_global_createEventFragment)
        }

        buttonNavToBackUpcomingEventPage.setOnClickListener {
            findNavController().navigate(R.id.action_global_homeFragment)
        }

        rvEventsList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = AdapterEvent(requireContext(), eventsData)
        rvEventsList.adapter = adapter

        vmEvent.events.observe(viewLifecycleOwner, { eventList ->
            eventsData.clear()
            eventsData.addAll(eventList)
            adapter.notifyDataSetChanged()
        })

        return v
    }
}
