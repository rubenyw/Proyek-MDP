package com.example.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class AdapterMyEvent(var context: Context, var arr: List<EventClassUI>, private val navController: NavController)
    : RecyclerView.Adapter<AdapterMyEvent.DpHolder>() {

    class DpHolder(it: View) : RecyclerView.ViewHolder(it) {
        var txtName: TextView = it.findViewById(R.id.tvEventName2)
        var txtLocation: TextView = it.findViewById(R.id.tvLocation2)
        var imageEvent: ImageView = it.findViewById(R.id.imageViewEvent2)
        var cancelBtn: Button = it.findViewById(R.id.btnCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DpHolder {
        var convertview = LayoutInflater.from(context).inflate(R.layout.rv_event_item2, parent, false)
        return DpHolder(convertview)
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    fun updateData(newData: List<EventClassUI>) {
        arr = newData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DpHolder, position: Int) {
        holder.txtName.setText(arr[position].name.toString())
        holder.txtLocation.setText(arr[position].location.toString())
        // Load image from URL using Glide
        Glide.with(context)
            .load(arr[position].urlLink)
            .into(holder.imageEvent)
        holder.cancelBtn.setOnClickListener {
            val id = arr[position].id
            val db = FirebaseFirestore.getInstance()
            val eventsRef = db.collection("events")
            val participantsRef = db.collection("event_participants")
            val donationHistoryRef = db.collection("donationHistory")
            val usersRef = db.collection("users")

            eventsRef.document(id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show()
                    arr = arr.toMutableList().apply { removeAt(position) }
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, arr.size)

                    participantsRef.whereEqualTo("eventId", id)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                document.reference.delete()
                            }
                            Toast.makeText(context, "Participants deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error deleting participants: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                    donationHistoryRef.whereEqualTo("eventId", id)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                val userId = document.getString("userId")
                                val amountString = document.getString("ammount") ?: "0"
                                val amount = amountString.toDouble()
                                if (userId != null) {
                                    usersRef.document(userId)
                                        .get()
                                        .addOnSuccessListener { userDoc ->
                                            if (userDoc.exists()) {
                                                val currentSaldo = userDoc.getDouble("saldo") ?: 0.0
                                                val newSaldo = currentSaldo + amount
                                                userDoc.reference.update("saldo", newSaldo)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(context, "User saldo updated", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(context, "Error updating saldo: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                        }
                                }
                                document.reference.delete()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error processing donation history: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error deleting event: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }
}