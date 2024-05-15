package com.example.project

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

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
            .whereEqualTo("password", password)
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
}