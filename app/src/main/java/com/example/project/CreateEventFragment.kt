package com.example.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class CreateEventFragment : Fragment() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri

    lateinit var backButton: Button
    lateinit var eventNameInput: EditText
    lateinit var eventDescriptionInput: EditText
    lateinit var eventDonationInput: EditText
    lateinit var eventLocationInput: EditText
    lateinit var eventDateTimeInput: EditText
    lateinit var eventImageButton: Button
    lateinit var eventImageName: TextView
    lateinit var createEventButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_create_event, container, false)

        backButton = v.findViewById(R.id.buttonBackCreateEvent)
        eventNameInput = v.findViewById(R.id.etEventName)
        eventDescriptionInput = v.findViewById(R.id.etEventDescription)
        eventDonationInput = v.findViewById(R.id.etDonationCreateEvent)
        eventLocationInput = v.findViewById(R.id.etLocationCreateEvent)
        eventDateTimeInput = v.findViewById(R.id.etDateTimeCreateEvent)
        eventImageButton = v.findViewById(R.id.buttonUploadImageCreateEventPage)
        eventImageName = v.findViewById(R.id.tvImageNameCreateEvent)
        createEventButton = v.findViewById(R.id.buttonCreateEventCreateEventPage)

        eventImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        createEventButton.setOnClickListener {
            uploadEvent()
        }

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_upcomingEventsFragment)
        }

        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data!!
            eventImageName.text = imageUri.lastPathSegment
        }
    }

    private fun uploadEvent() {
        val eventName = eventNameInput.text.toString()
        val eventDescription = eventDescriptionInput.text.toString()
        val eventDonation = eventDonationInput.text.toString().toDoubleOrNull() ?: 0.0
        val eventLocation = eventLocationInput.text.toString()
        val eventDateTime = eventDateTimeInput.text.toString()

        if (this::imageUri.isInitialized) {
            val storageRef = FirebaseStorage.getInstance().reference.child("event_images/${UUID.randomUUID()}")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveEventToFirestore(eventName, eventDescription, eventDonation, eventLocation, eventDateTime, uri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            saveEventToFirestore(eventName, eventDescription, eventDonation, eventLocation, eventDateTime, "")
        }
    }

    private fun saveEventToFirestore(name: String, description: String, donation: Double, location: String, dateTime: String, imageUrl: String) {
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
                Toast.makeText(context, "Event created successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_global_upcomingEventsFragment)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Event creation failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}