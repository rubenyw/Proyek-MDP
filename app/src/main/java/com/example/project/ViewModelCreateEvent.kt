import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ViewModelCreateEvent : ViewModel() {
    private val _eventCreated = MutableLiveData<Boolean>()
    val eventCreated: LiveData<Boolean> get() = _eventCreated

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun uploadImageAndSaveEvent(imageUri: Uri, name: String, description: String, donation: Double, location: String, dateTime: Timestamp, userId: String, userBalance: Double) {
        _isLoading.value = true
        val storageRef = FirebaseStorage.getInstance().reference.child("event_images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveEvent(name, description, donation, location, dateTime, uri.toString(), userId, userBalance, 0)
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                _eventCreated.value = false
            }
    }

    fun saveEvent(name: String, description: String, donation: Double, location: String, dateTime: Timestamp, imageUrl: String, userId: String, userBalance: Double, participants: Int) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.runTransaction { transaction ->
            val userDocRef = firestore.collection("users").document(userId)
            val userSnapshot = transaction.get(userDocRef)
            val currentBalance = userSnapshot.getDouble("saldo") ?: 0.0

            if (currentBalance >= donation) {
                val newBalance = currentBalance - donation
                transaction.update(userDocRef, "saldo", newBalance)

                val eventDocRef = firestore.collection("events").document()
                val event = hashMapOf(
                    "name" to name,
                    "description" to description,
                    "donation" to donation,
                    "location" to location,
                    "dateTime" to dateTime,
                    "imageUrl" to imageUrl,
                    "participants" to participants,
                    "creator" to userId,
                )
                transaction.set(eventDocRef, event)

                val participantData = hashMapOf(
                    "userId" to userId,
                    "eventId" to eventDocRef.id
                )
                transaction.set(firestore.collection("event_participants").document(), participantData)

                val newParticipants = participants + 1
                transaction.update(eventDocRef, "participants", newParticipants)

                if (donation > 0) {
                    val donationHistoryData = hashMapOf(
                        "ammount" to donation.toString(),
                        "eventId" to eventDocRef.id,
                        "message" to "Thank you for your donation!",
                        "time" to dateTime,
                        "userId" to userId
                    )
                    transaction.set(firestore.collection("donationHistory").document(), donationHistoryData)
                }

                true
            } else {
                false
            }
        }.addOnSuccessListener { success ->
            _isLoading.value = false
            _eventCreated.value = success
        }.addOnFailureListener {
            _isLoading.value = false
            _eventCreated.value = false
        }
    }
}
