package com.example.project

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.project.databinding.FragmentLoginBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    private lateinit var db: AppDatabase
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        // Initialize the database
        db = AppDatabase.build(this.requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLoginLogin.setOnClickListener {
            val fields = arrayListOf(
                binding.etUsernameLogin.text.toString(),
                binding.etPasswordLogin.text.toString(),
            )

            if (Mekanisme.isEmptyField(fields)) {
                Mekanisme.showToast(requireContext(), "Pastikan semua field terisi!")
            } else {
                Mekanisme.authenticate(fields[0], fields[1]) { isValid, documentId ->
                    if (isValid) {
                        if (documentId != null) {
                            firestore.collection("users").document(documentId).get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val username = document.getString("username")
                                        val name = document.getString("name")

                                        val userLogin = UserEntity(
                                            id = documentId,
                                            username = username ?: "",
                                            name = name ?: ""
                                        )

                                        coroutine.launch {
                                            db.userDAO().deleteAll();
                                            db.userDAO().insert(userLogin)
                                            withContext(Dispatchers.Main) {
                                                val intent = Intent(requireActivity(), MainActivity::class.java)
                                                startActivity(intent)
                                                Mekanisme.showToast(requireContext(), "Login berhasil")
                                            }
                                        }
                                    }
                                }
                        }
                    } else {
                        Mekanisme.showToast(requireContext(), "Couldn't find your account. Recheck your username and password!")
                    }
                }
            }
        }
    }
}
