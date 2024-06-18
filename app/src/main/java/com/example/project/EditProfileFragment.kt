package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditProfileFragment : Fragment() {
    private lateinit var db: AppDatabase
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    lateinit var tvUsername: TextView
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirm: EditText
    lateinit var btnUpdate: Button
    private var userId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUsername = view.findViewById(R.id.edit_username_tv)
        etName = view.findViewById(R.id.edit_fullname_et)
        etEmail = view.findViewById(R.id.edit_email_et)
        etPassword = view.findViewById(R.id.edit_password_et)
        etConfirm = view.findViewById(R.id.edit_confirm_et)
        btnUpdate = view.findViewById(R.id.edit_update_btn)
        db = AppDatabase.build(this.requireActivity())
        firestore = FirebaseFirestore.getInstance()

        coroutine.launch {
            val users = db.userDAO().fetch()
            if (users.isNotEmpty()) {
                userId = users[0].id
                val name = users[0].name
                withContext(Dispatchers.Main) {
                    tvUsername.text = name
                }
            }
        }

        btnUpdate.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newEmail = etEmail.text.toString().trim()
            val newPassword = etPassword.text.toString().trim()
            val confirmPassword = etConfirm.text.toString().trim()
            if (newPassword == confirmPassword) {
                updateUserData(newName, newEmail, newPassword)
            } else {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserData(newName: String, newEmail: String, newPassword: String) {
        coroutine.launch {
            val updates = hashMapOf<String, Any>()
            if (newName.isNotBlank()) {
                updates["name"] = newName
            }
            if (newEmail.isNotBlank()) {
                updates["email"] = newEmail
            }
            if (newPassword.isNotBlank()) {
                updates["password"] = newPassword
            }

            if (updates.isNotEmpty() && userId != null) {
                try {
                    firestore.collection("users").document(userId!!).update(updates).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_global_profileFragment)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
