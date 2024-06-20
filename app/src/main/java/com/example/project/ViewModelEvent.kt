package com.example.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewModelEvent : ViewModel() {

    private val _events = MutableLiveData<List<EventClassUI>>()
    val events: LiveData<List<EventClassUI>> get() = _events

    private val db = FirebaseFirestore.getInstance()

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        val currentDateTime = Date()
        db.collection("events")
            .whereGreaterThan("dateTime", currentDateTime)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val eventsData = ArrayList<EventClassUI>()
                    for (document in task.result) {
                        val timestamp = document.getTimestamp("dateTime") // Ensure the field name matches your Firestore document
                        val date = timestamp?.toDate()
                        val formattedDate = date?.let { formatDate(it) } ?: "Unknown date"

                        val event = EventClassUI(
                            document.id,
                            document.getString("name") ?: "",
                            formattedDate,
                            document.getString("location") ?: "",
                            document.getString("description") ?: "",
                            document.getString("imageUrl") ?: "",
                            document.getLong("donation")?.toInt() ?: 0,
                        )
                        eventsData.add(event)
                    }
                    _events.value = eventsData
                } else {
                    println("Error getting documents: ${task.exception}")
                }
            }
    }

    fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) // Adjust the date format as needed
        return format.format(date)
    }

    fun formatRupiah(amount: Int): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(amount)
    }

    fun processDonation(userId: String, eventId: String, donationAmount: Int, onSuccess: (newDonation: Long) -> Unit, onFailure: (errorMessage: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userDoc = db.collection("users").document(userId).get().await()
                val eventDoc = db.collection("events").document(eventId).get().await()

                if (userDoc.exists() && eventDoc.exists()) {
                    val userSaldo = userDoc.getLong("saldo") ?: 0L
                    val currentDonation = eventDoc.getLong("donation") ?: 0L

                    if (userSaldo >= donationAmount) {
                        val newSaldo = userSaldo - donationAmount
                        val newDonation = currentDonation + donationAmount

                        db.runBatch { batch ->
                            batch.update(db.collection("users").document(userId), "saldo", newSaldo)
                            batch.update(db.collection("events").document(eventId), "donation", newDonation)
                        }.await()

                        withContext(Dispatchers.Main) {
                            onSuccess(newDonation)
                        }

                        val historyDonation = hashMapOf(
                            "ammount" to donationAmount.toString(),
                            "eventId" to eventId,
                            "message" to "HAi",
                            "time" to Timestamp.now(),
                            "userId" to userId
                        )
                        db.collection("donationHistory")
                            .add(historyDonation);
                    } else {
                        withContext(Dispatchers.Main) {
                            onFailure("Insufficient balance to make the donation.")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onFailure("User or event data not found.")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onFailure("Failed to process donation. Please try again.")
                }
            }
        }
    }
}
