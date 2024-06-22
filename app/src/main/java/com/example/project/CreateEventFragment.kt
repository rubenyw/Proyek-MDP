package com.example.project

import ViewModelCreateEvent
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
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    lateinit var progressBar: ProgressBar
    private lateinit var progressMessage: TextView
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var db: AppDatabase
    private lateinit var userId: String
    private lateinit var firestore: FirebaseFirestore

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val vmEvent: ViewModelCreateEvent by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_create_event, container, false)

        db = AppDatabase.build(this.requireActivity())
        firestore = FirebaseFirestore.getInstance()
        coroutine.launch {
            val users = db.userDAO().fetch()
            if (users.isNotEmpty()) {
                val name = users[0].name
                userId = users[0].id
                fetchUserData(userId)
                withContext(Dispatchers.Main) {

                }
            }
        }

        backButton = v.findViewById(R.id.buttonBackCreateEvent)
        eventNameInput = v.findViewById(R.id.etEventName)
        eventDescriptionInput = v.findViewById(R.id.etEventDescription)
        eventDonationInput = v.findViewById(R.id.etDonationCreateEvent)
        eventLocationInput = v.findViewById(R.id.etLocationCreateEvent)
        eventDateTimeInput = v.findViewById(R.id.etDateTimeCreateEvent)
        eventImageButton = v.findViewById(R.id.buttonUploadImageCreateEventPage)
        eventImageName = v.findViewById(R.id.tvImageNameCreateEvent)
        createEventButton = v.findViewById(R.id.buttonCreateEventCreateEventPage)
        progressBar = v.findViewById(R.id.progressBar)
        progressMessage = v.findViewById(R.id.progressMessage)

        eventImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        createEventButton.setOnClickListener {
            showLoading("Uploading event details...")
            uploadEvent()
        }

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_upcomingEventsFragment)
        }

        eventDateTimeInput.setOnClickListener {
            showDatePickerDialog()
        }

        vmEvent.isLoading.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        vmEvent.eventCreated.observe(viewLifecycleOwner, { eventCreated ->
            if (eventCreated) {
                findNavController().navigate(R.id.action_global_upcomingEventsFragment)
            } else {
                Toast.makeText(context, "Failed to create event", Toast.LENGTH_SHORT).show()
            }
        })

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

    private fun showLoading(message: String) {
        progressBar.visibility = View.VISIBLE
        progressMessage.text = message
        progressMessage.visibility = View.VISIBLE
        setInputsEnabled(false)
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
        progressMessage.visibility = View.GONE
        setInputsEnabled(true)
    }

    private fun setInputsEnabled(enabled: Boolean) {
        backButton.isEnabled = enabled
        eventNameInput.isEnabled = enabled
        eventDescriptionInput.isEnabled = enabled
        eventDonationInput.isEnabled = enabled
        eventLocationInput.isEnabled = enabled
        eventDateTimeInput.isEnabled = enabled
        eventImageButton.isEnabled = enabled
        createEventButton.isEnabled = enabled
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
            hideLoading()
            return
        }
        val eventTimestamp = Timestamp(eventDate)

        coroutine.launch {
            val userDocument = firestore.collection("users").document(userId).get().await()
            val userBalance = userDocument.getDouble("saldo") ?: 0.0

            if (userBalance >= eventDonation) {
                withContext(Dispatchers.Main) {
                    if (this@CreateEventFragment::imageUri.isInitialized) {
                        vmEvent.uploadImageAndSaveEvent(imageUri, eventName, eventDescription, eventDonation, eventLocation, eventTimestamp, userId, userBalance)
                    } else {
                        vmEvent.saveEvent(eventName, eventDescription, eventDonation, eventLocation, eventTimestamp, "", userId, userBalance, 0)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Insufficient balance", Toast.LENGTH_SHORT).show()
                    hideLoading()
                }
            }
        }
    }

    private suspend fun fetchUserData(id: String) {
        try {
            val documentSnapshot = firestore.collection("users").document(id).get().await()
            if (documentSnapshot.exists()) {
                val userData = documentSnapshot.data
                withContext(Dispatchers.Main) {

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
