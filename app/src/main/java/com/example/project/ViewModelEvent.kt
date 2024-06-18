package com.example.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewModelEvent : ViewModel() {

    private val _events = MutableLiveData<List<EventClass>>()
    val events: LiveData<List<EventClass>> get() = _events

    private val db = FirebaseFirestore.getInstance()

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        db.collection("events")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val eventsData = ArrayList<EventClass>()
                    for (document in task.result) {
                        val timestamp = document.getTimestamp("date")
                        val date = timestamp?.toDate()
                        val formattedDate = date?.let { formatDate(it) } ?: "Unknown date"

                        val event = EventClass(
                            document.id,
                            document.getString("name") ?: "",
                            formattedDate,
                            document.getString("location") ?: "",
                            document.getString("description") ?: "",
                            document.getString("imageUrl") ?: ""
                        )
                        eventsData.add(event)
                    }
                    _events.value = eventsData
                } else {
                    println("Error getting documents: ${task.exception}")
                }
            }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(date)
    }
}
