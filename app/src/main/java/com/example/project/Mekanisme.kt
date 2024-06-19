package com.example.project

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

object Mekanisme {
//    var userLoggedIn: UserEntity? = null;
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, message, duration).show()
    }

    fun isEmptyField(listField: ArrayList<String>): Boolean {
        for (field in listField) {
            if (field.isNullOrEmpty()) return true;
        }
        return false;
    }

    fun authenticate(username: String,password: String, completion: (Boolean,String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")
        val usernameQuery = usersCollection
            .whereEqualTo("username", username)
            .whereEqualTo("password", password);

        usernameQuery.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    completion(false, null)
                } else {
                    val document = documents.first()
                    val documentId = document.id
                    completion(true, documentId)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error checking username", exception)
                completion(false,null)
            }
    }

    fun checkUsername(username: String, completion: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        val usernameQuery = usersCollection.whereEqualTo("username", username)

        usernameQuery.get()
            .addOnSuccessListener { documents ->
                val isUsernameAvailable = documents.isEmpty  // Username is available if no documents found
                completion(isUsernameAvailable)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error checking username", exception)
                completion(false) // Indicate an error by returning false (or handle differently)
            }
    }

    fun formatToRupiah(amount: Number): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return when (amount) {
            is Int -> numberFormat.format(amount.toDouble())
            is Long -> numberFormat.format(amount.toDouble())
            is Double -> numberFormat.format(amount)
            else -> throw IllegalArgumentException("Unsupported number type")
        }
    }
}