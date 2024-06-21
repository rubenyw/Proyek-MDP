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

    fun uploadImageAndSaveEvent(imageUri: Uri, name: String, description: String, donation: Double, location: String, dateTime: Timestamp, userId: String) {
        _isLoading.value = true
        val storageRef = FirebaseStorage.getInstance().reference.child("event_images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveEvent(name, description, donation, location, dateTime, uri.toString(), userId, 0)
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                _eventCreated.value = false
            }
    }

    fun saveEvent(name: String, description: String, donation: Double, location: String, dateTime: Timestamp, imageUrl: String, userId: String, participants: Int) {
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

        val firestore = FirebaseFirestore.getInstance()
        val eventCollection = firestore.collection("events")
        firestore.runBatch { batch ->
            val eventDocRef = eventCollection.document()
            batch.set(eventDocRef, event)

            val participantData = hashMapOf(
                "userId" to userId,
                "eventId" to eventDocRef.id
            )
            batch.set(firestore.collection("event_participants").document(), participantData)

            val newParticipants = participants + 1
            batch.update(eventDocRef, "participants", newParticipants)
        }.addOnSuccessListener {
            _isLoading.value = false
            _eventCreated.value = true
        }.addOnFailureListener {
            _isLoading.value = false
            _eventCreated.value = false
        }
    }
}
