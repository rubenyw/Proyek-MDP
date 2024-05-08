package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class login : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    lateinit var etUsername:EditText
    lateinit var etPassword:EditText
    lateinit var btnLogin:Button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etUsername = view.findViewById(R.id.etUsername_login)
        etPassword = view.findViewById(R.id.etPassword_login)
        btnLogin = view.findViewById(R.id.btnLogin_login)
        btnLogin.setOnClickListener {
            if(etUsername.text.isNullOrEmpty() ||etPassword.text.isNullOrEmpty()){
                Toast.makeText(requireContext(),  "Pastikan semua field terisi!", Toast.LENGTH_SHORT).show()
            }else{
                authenticate(etUsername.text.toString(),etPassword.text.toString()) { isValid, documentId->
                    if(isValid){
                        val intent = Intent(requireActivity(), MainActivity2::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(requireContext(),  "Couldn't found your account, Recheck your username and password!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
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