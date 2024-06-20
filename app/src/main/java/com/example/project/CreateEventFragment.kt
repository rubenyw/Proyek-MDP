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
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.fragment.app.viewModels
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

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

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val vmEvent: ViewModelCreateEvent by viewModels()

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

        eventDateTimeInput.setOnClickListener {
            showDatePickerDialog()
        }

        return v
    }

    fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                eventDateTimeInput.setText(dateFormat.format(calendar.time))
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show()
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show()
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

        val eventDate: Date
        try {
            eventDate = dateFormat.parse(eventDateTime)!!
        } catch (e: Exception) {
            Toast.makeText(context, "Invalid date and time format", Toast.LENGTH_SHORT).show()
            return
        }
        val eventTimestamp = Timestamp(eventDate)

        if (this::imageUri.isInitialized) {
            vmEvent.uploadImageAndSaveEvent(imageUri, eventName, eventDescription, eventDonation, eventLocation, eventTimestamp)
        } else {
            vmEvent.saveEvent(eventName, eventDescription, eventDonation, eventLocation, eventTimestamp, "")
        }
    }
}