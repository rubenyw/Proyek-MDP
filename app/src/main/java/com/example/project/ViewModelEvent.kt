package com.example.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Date
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class ViewModelEvent : ViewModel() {

    private val _events = MutableLiveData<List<EventClass>>()
    val events: LiveData<List<EventClass>> = _events

    private val db = FirebaseFirestore.getInstance()

    fun getEvents() : ArrayList<EventClass>{
        var eventsData = ArrayList<EventClass>()
        db.collection("events") // Replace "events" with your collection name
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val event = EventClass(
                            document.id,
                            document.data["name"].toString(),
                            "12-07-2002",
                            document.data["location"].toString(),
                            document.data["description"].toString()
                        )
                        eventsData.add(event)
                    }
                    _events.value = eventsData.toMutableList()
                } else {
                    println("Error getting documents: ${task.exception}")
                }
            })
        return eventsData
    }
}