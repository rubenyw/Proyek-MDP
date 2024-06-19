package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
    private lateinit var buttonDonate: Button
    private lateinit var buttonJoin: Button
    private lateinit var eventId: String
    private lateinit var eventName: String
    private lateinit var eventLocation: String
    private lateinit var eventDate: String
    private lateinit var eventDescription: String
    private lateinit var eventImageUrl: String
    private lateinit var eventDonation: String
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var db: AppDatabase
    private lateinit var userId: String
    private lateinit var firestore: FirebaseFirestore

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
        }
    }

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

        val vmEvent: ViewModelEvent by viewModels()

        buttonBack = v.findViewById(R.id.floatingActionButtonBackEventDetail)
        buttonDonate = v.findViewById(R.id.buttonDonateEventDetail)
        buttonJoin = v.findViewById(R.id.buttonJoinEventDetails)
        imageViewEventImage = v.findViewById(R.id.imageViewEventDetailPage)
        tvTitle = v.findViewById(R.id.tvTitleEventDetailPage)
        tvDescription = v.findViewById(R.id.tvDescriptionEventDetailPage)
        tvDonation = v.findViewById(R.id.tvDonationEventDetail)
        tvLocation = v.findViewById(R.id.tvLocationEventDetail)
        tvSchedule = v.findViewById(R.id.tvDateTimeEventDetailPage)

        tvTitle.setText(eventName)
        tvDescription.setText(eventDescription)
        tvSchedule.setText(eventDate)
        tvLocation.setText(eventLocation)
        tvDonation.text = vmEvent.formatRupiah(eventDonation.toInt())

        Glide.with(this)
            .load(eventImageUrl)
            .into(imageViewEventImage)

        buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_global_upcomingEventsFragment)
        }

        buttonDonate.setOnClickListener {

        }

        buttonJoin.setOnClickListener {
            coroutine.launch {
                saveEventParticipant(userId, eventId)
            }
        }

        return v
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

    private suspend fun saveEventParticipant(userId: String, eventId: String) {
        try {
            val participantData = hashMapOf(
                "userId" to userId,
                "eventId" to eventId
            )
            firestore.collection("event_participants")
                .add(participantData)
                .await()
            withContext(Dispatchers.Main) {
                // Notify the user about successful participation
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                // Notify the user about the failure
            }
        }
    }
}