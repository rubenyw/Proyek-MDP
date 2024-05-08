package com.example.project

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

class register : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    lateinit var etName:EditText
    lateinit var etUsername:EditText
    lateinit var etEmail:EditText
    lateinit var etPassword:EditText
    lateinit var etConfirm:EditText
    lateinit var btnRegister:Button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etName = view.findViewById(R.id.etFullname_register)
        etUsername = view.findViewById(R.id.etUsername_register)
        etEmail = view.findViewById(R.id.etEmail_register)
        etPassword = view.findViewById(R.id.etPassword_register)
        etConfirm = view.findViewById(R.id.etConfirm_register)
        btnRegister = view.findViewById(R.id.btnRegister_register)
        btnRegister.setOnClickListener {
            if(etName.text.isNullOrEmpty()||etUsername.text.isNullOrEmpty()||etEmail.text.isNullOrEmpty()||etPassword.text.isNullOrEmpty()||etConfirm.text.isNullOrEmpty()){
                Toast.makeText(requireContext(),  "Pastikan semua field terisi!", Toast.LENGTH_SHORT).show()
            }
            else if(etPassword.text.toString() != etConfirm.text.toString()){
                Toast.makeText(requireContext(),  "Pastikan confirm password sesuai dengan password anda!", Toast.LENGTH_SHORT).show()
            }
            else{
                checkUsername(etUsername.text.toString()) { isUsernameAvailable ->
                    if (isUsernameAvailable) {
                        val db = FirebaseFirestore.getInstance()
                        val usersCollection = db.collection("users")
                        val user = hashMapOf(
                            "name" to etName.text.toString(),
                            "username" to etUsername.text.toString(),
                            "email" to etEmail.text.toString(),
                            "password" to etPassword.text.toString(),
                            "saldo" to 0,
                            "rank" to 5
                        )
                        usersCollection.add(user)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(requireContext(), "User created successfully!", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_global_login)
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error adding document", e)
                                Toast.makeText(requireContext(), "Fail to create new user :(", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(requireContext(), "Username has been taken, Try something else", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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

}