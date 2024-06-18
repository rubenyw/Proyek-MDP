package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var db: AppDatabase
    private val coroutine = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    lateinit var tvName: TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the database
        db = AppDatabase.build(this.requireActivity())

        tvName = view.findViewById(R.id.home_username_tv)
        coroutine.launch {
            val users = db.userDAO().fetch()
            if (users.isNotEmpty()) {
                val name = users[0].name
                withContext(Dispatchers.Main) {
                    tvName.text = name
                }
            }
        }
        btnEvent = view.findViewById(R.id.btnEvent)
        btnEvent.setOnClickListener {
            findNavController().navigate(R.id.action_global_eventFragment)
        }
        btnCommunity = view.findViewById(R.id.btnCommunity)
        btnCommunity.setOnClickListener {
            findNavController().navigate(R.id.action_global_myCommunitiesFragment)
        }
    }
}
