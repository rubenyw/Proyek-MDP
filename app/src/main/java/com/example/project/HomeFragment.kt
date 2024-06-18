package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    lateinit var btnEvent: Button
    lateinit var btnCommunity: Button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnEvent = view.findViewById(R.id.btnEvent)
        btnEvent.setOnClickListener {
            findNavController().navigate(R.id.action_global_upcomingEventsFragment)
        }
        btnCommunity = view.findViewById(R.id.btnCommunity)
        btnCommunity.setOnClickListener {
            findNavController().navigate(R.id.action_global_myCommunitiesFragment)
        }
    }
}