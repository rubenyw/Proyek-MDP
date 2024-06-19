package com.example.project

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
class participationBookFragment : Fragment() {
    private lateinit var db: AppDatabase
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_participation_book, container, false)
    }

    lateinit var btnUpcoming: Button
    lateinit var btnHistory: Button
    lateinit var rvEvent: RecyclerView
    private lateinit var adapter: AdapterEvent2
    private var eventsData = ArrayList<EventClassUI>()
    private var upcomingData = ArrayList<EventClassUI>()
    private var historyData = ArrayList<EventClassUI>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("EventData", "onViewCreated called")

        db = AppDatabase.build(this.requireActivity())
        firestore = FirebaseFirestore.getInstance()

        btnUpcoming = view.findViewById(R.id.participation_upcoming_btn)
        btnHistory = view.findViewById(R.id.participation_history_btn)
        rvEvent = view.findViewById(R.id.participation_rv)

        adapter = AdapterEvent2(requireContext(), upcomingData, findNavController())
        rvEvent.adapter = adapter
        rvEvent.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        btnUpcoming.setBackgroundColor(resources.getColor(R.color.default_button_color))
        btnHistory.setBackgroundColor(resources.getColor(R.color.gray))

        btnUpcoming.setOnClickListener {
            btnUpcoming.setBackgroundColor(resources.getColor(R.color.default_button_color))
            btnHistory.setBackgroundColor(resources.getColor(R.color.gray))
            adapter.updateData(upcomingData)
        }

        btnHistory.setOnClickListener {
            btnHistory.setBackgroundColor(resources.getColor(R.color.default_button_color))
            btnUpcoming.setBackgroundColor(resources.getColor(R.color.gray))
            adapter.updateData(historyData)
        }

        coroutine.launch {
            val users = db.userDAO().fetch()
            Log.d("EventData", "Fetched users from local database")
            if (users.isNotEmpty()) {
                userId = users[0].id
                Log.d("EventData", "User ID: $userId")
                fetchUserEvents(userId)
            } else {
                Log.d("EventData", "No users found in local database")
            }
        }
    }

    private suspend fun fetchUserEvents(userId: String) {
        try {
            val eventParticipantSnapshot = firestore.collection("event_participants")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            Log.d("EventData", "Fetched event participants")

            val eventIds = eventParticipantSnapshot.documents.map { it.getString("eventId") }

            val eventFetchTasks = eventIds.mapNotNull { eventId ->
                firestore.collection("events").document(eventId!!).get()
            }

            val eventSnapshots = eventFetchTasks.map { it.await() }

            eventsData.clear()
            for (document in eventSnapshots) {
                if (document.exists()) {
                    val timestamp = document.getTimestamp("dateTime") // Ensure the field name matches your Firestore document
                    val date = timestamp?.toDate()
                    val formattedDate = date?.let { formatDate(it) } ?: "Unknown date"

                    val event = EventClassUI(
                        document.id,
                        document.getString("name") ?: "",
                        formattedDate,
                        document.getString("location") ?: "",
                        document.getString("description") ?: "",
                        document.getString("imageUrl") ?: ""
                    )
                    eventsData.add(event)
                }
            }
            Log.d("EventData", "Number of events fetched: ${eventsData.size}")

            sortEventsByDate()

            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.e("EventData", "Error fetching events: ${e.message}")
            }
        }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) // Adjust the date format as needed
        return format.format(date)
    }

    private fun sortEventsByDate() {
        val today = Calendar.getInstance().time

        upcomingData.clear()
        historyData.clear()

        eventsData.forEach { event ->
            try {
                val eventDate = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).parse(event.date)
                if (eventDate != null) {
                    if (eventDate.after(today)) {
                        upcomingData.add(event)
                    } else {
                        historyData.add(event)
                    }
                }
            } catch (e: Exception) {
                Log.e("EventData", "Error parsing date: ${e.message}")
            }
        }

        Log.d("EventData", "Upcoming events: ${upcomingData.size}")
        Log.d("EventData", "History events: ${historyData.size}")
    }
}
