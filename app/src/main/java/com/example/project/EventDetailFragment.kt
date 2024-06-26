package com.example.project

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class EventDetailFragment : Fragment() {

    private lateinit var buttonBack: FloatingActionButton
    private lateinit var imageViewEventImage: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvSchedule: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDonation: TextView
    private lateinit var tvParticipants: TextView
    private lateinit var buttonDonate: Button
    private lateinit var buttonJoin: Button
    private lateinit var editTextDonationAmount: EditText
    private lateinit var eventId: String
    private lateinit var eventName: String
    private lateinit var eventLocation: String
    private lateinit var eventDate: String
    private lateinit var eventDescription: String
    private lateinit var eventImageUrl: String
    private lateinit var eventDonation: String
    private lateinit var eventParticipants: String
    private lateinit var eventCreatorId: String
    private lateinit var userId: String
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var db: AppDatabase
    private lateinit var firestore: FirebaseFirestore
    private val vmEvent: ViewModelEvent by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            eventId = it.getString("eventId") ?: ""
            eventName = it.getString("eventName") ?: ""
            eventLocation = it.getString("eventLocation") ?: ""
            eventDate = it.getString("eventDate") ?: ""
            eventDescription = it.getString("eventDescription") ?: ""
            eventImageUrl = it.getString("eventImageUrl") ?: ""
            eventDonation = it.getInt("eventDonation").toString() ?: ""
            eventParticipants = it.getInt("eventParticipants").toString() ?: ""
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_event_detail, container, false)

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

        buttonBack = v.findViewById(R.id.floatingActionButtonBackEventDetail)
        buttonDonate = v.findViewById(R.id.buttonDonateEventDetail)
        buttonJoin = v.findViewById(R.id.buttonJoinEventDetails)
        imageViewEventImage = v.findViewById(R.id.imageViewEventDetailPage)
        tvTitle = v.findViewById(R.id.tvTitleEventDetailPage)
        tvDescription = v.findViewById(R.id.tvDescriptionEventDetailPage)
        tvDonation = v.findViewById(R.id.tvDonationEventDetail)
        tvLocation = v.findViewById(R.id.tvLocationEventDetail)
        tvSchedule = v.findViewById(R.id.tvDateTimeEventDetailPage)
        tvParticipants = v.findViewById(R.id.tvParticipantsEventDetail)
        editTextDonationAmount = v.findViewById(R.id.editTextNumberSigned2)

        tvTitle.setText(eventName)
        tvDescription.setText(eventDescription)
        tvSchedule.setText(eventDate)
        tvLocation.setText(eventLocation)
        tvDonation.text = vmEvent.formatRupiah(eventDonation.toInt())
        tvParticipants.setText(eventParticipants)

        Glide.with(this)
            .load(eventImageUrl)
            .into(imageViewEventImage)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_global_upcomingEventsFragment)
        }

        buttonDonate.setOnClickListener {
            val donationAmount = editTextDonationAmount.text.toString().toIntOrNull()
            if (donationAmount != null) {
                buttonDonate.isEnabled = false;
                buttonDonate.text = "Donating...";
                coroutine.launch {
                    vmEvent.processDonation(userId, eventId, donationAmount,
                        onSuccess = { newDonation ->
                            tvDonation.text = vmEvent.formatRupiah(newDonation.toInt())
                            Toast.makeText(context, "Donation successful!", Toast.LENGTH_SHORT).show();
                            buttonDonate.isEnabled = true;
                            buttonDonate.text = "Donate";
                            editTextDonationAmount.setText("");
                        },
                        onFailure = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            buttonDonate.isEnabled = true;
                            buttonDonate.text = "Donate";
                            editTextDonationAmount.setText("");
                        }
                    )
                }
            }

        }

        checkUserParticipation()
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

    private fun checkUserParticipation() {
        coroutine.launch {
            try {
                val participants = firestore.collection("event_participants")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("eventId", eventId)
                    .get()
                    .await()

                withContext(Dispatchers.Main) {
                    if (participants.isEmpty) {
                        buttonJoin.text = "Join"
                        buttonJoin.setOnClickListener {
                            coroutine.launch {
                                joinEvent()
                            }
                        }
                    } else {
                        buttonJoin.text = "Leave"
                        buttonJoin.setOnClickListener {
                            coroutine.launch {
                                leaveEvent()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun joinEvent() {
        try {
            val eventDocRef = firestore.collection("events").document(eventId)
            val eventDoc = eventDocRef.get().await()
            val currentParticipants = eventDoc.getLong("participants") ?: 0L
            val newParticipants = currentParticipants + 1

            val participantData = hashMapOf(
                "userId" to userId,
                "eventId" to eventId
            )

            firestore.runBatch { batch ->
                batch.set(firestore.collection("event_participants").document(), participantData)
                batch.update(eventDocRef, "participants", newParticipants)
            }.await()

            withContext(Dispatchers.Main) {
                buttonJoin.text = "Leave"
                buttonJoin.setOnClickListener {
                    coroutine.launch {
                        leaveEvent()
                    }
                }
                tvParticipants.text = newParticipants.toString()  // Update UI
                Toast.makeText(context, "Successfully joined the event!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to join the event. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun leaveEvent() {
        try {
            val eventDoc = firestore.collection("events").document(eventId).get().await()
            eventCreatorId = eventDoc.getString("creator") ?: ""

            if (userId == eventCreatorId) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "You cannot leave your own event.", Toast.LENGTH_SHORT).show()
                }
                return
            }

            val eventDocRef = firestore.collection("events").document(eventId)
            val currentParticipants = eventDoc.getLong("participants") ?: 0L
            val newParticipants = currentParticipants - 1

            val participantQuery = firestore.collection("event_participants")
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .get()
                .await()

            firestore.runBatch { batch ->
                for (document in participantQuery.documents) {
                    batch.delete(firestore.collection("event_participants").document(document.id))
                }
                batch.update(eventDocRef, "participants", newParticipants)
            }.await()

            withContext(Dispatchers.Main) {
                buttonJoin.text = "Join"
                buttonJoin.setOnClickListener {
                    coroutine.launch {
                        joinEvent()
                    }
                }
                tvParticipants.text = newParticipants.toString()  // Update UI
                Toast.makeText(context, "Successfully left the event.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to leave the event. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}