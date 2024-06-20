package com.example.project

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class ViewModelCreateEvent : ViewModel()  {
    private val _eventCreated = MutableLiveData<Boolean>()
    val eventCreated: LiveData<Boolean> get() = _eventCreated

    fun uploadImageAndSaveEvent(imageUri: Uri, name: String, description: String, donation: Double, location: String, dateTime: Timestamp) {
        val storageRef = FirebaseStorage.getInstance().reference.child("event_images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveEvent(name, description, donation, location, dateTime, uri.toString())
                }
            }
            .addOnFailureListener {
                _eventCreated.value = false
            }
    }

    fun saveEvent(name: String, description: String, donation: Double, location: String, dateTime: Timestamp, imageUrl: String) {
        val event = hashMapOf(
            "name" to name,
            "description" to description,
            "donation" to donation,
            "location" to location,
            "dateTime" to dateTime,
            "imageUrl" to imageUrl
        )

        FirebaseFirestore.getInstance().collection("events")
            .add(event)
            .addOnSuccessListener {
                _eventCreated.value = true
            }
            .addOnFailureListener {
                _eventCreated.value = false
            }
    }

}