package com.example.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ViewModelEvent : ViewModel() {

    private val _events = MutableLiveData<List<EventClass>>()
    val events: LiveData<List<EventClass>> = _events

    private val db = FirebaseFirestore.getInstance()

    fun getEvents() : ArrayList<EventClass>{
        var eventsData = ArrayList<EventClass>()
        db.collection("events") // Replace "events" with your collection name
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                val eventsList = mutableListOf<EventClass>()
                for (document in result) {
                    val event = document.toObject(EventClass::class.java)
                    event.id = document.id // Add the document ID to the event
                    eventsList.add(event)
                }
                _events.value = eventsList
                _events.value?.forEach{
                    eventsData.add(it)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ViewModelEvent", "Error getting documents: ", exception)
            }
        return eventsData
    }
}