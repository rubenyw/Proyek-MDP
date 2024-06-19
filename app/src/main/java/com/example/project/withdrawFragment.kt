package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class withdrawFragment : Fragment() {
    private lateinit var db: AppDatabase
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_withdraw, container, false)
    }

    lateinit var etAmmount: EditText
    lateinit var btnConfirm: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.build(this.requireActivity())
        firestore = FirebaseFirestore.getInstance()

        coroutine.launch {
            val users = db.userDAO().fetch()
            if (users.isNotEmpty()) {
                userId = users[0].id
            }
        }

        btnConfirm = view.findViewById(R.id.withdraw_withdraw_btn)
        etAmmount = view.findViewById(R.id.withdraw_ammount_et)

        btnConfirm.setOnClickListener {
            if (etAmmount.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Pastikan jumlah terisi!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val amount = etAmmount.text.toString().toDouble()
                updateSaldo(userId, amount)
            }
        }
    }

    private fun updateSaldo(userId: String, amount: Double) {
        coroutine.launch {
            try {
                val userRef = firestore.collection("users").document(userId)
                var transactionResult = false
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val currentSaldo = snapshot.getDouble("saldo")!!
                    if (currentSaldo >= amount) {
                        val newSaldo = currentSaldo - amount
                        transaction.update(userRef, "saldo", newSaldo)
                        transactionResult = true
                    } else {
                        transactionResult = false
                    }
                }.await()
                withContext(Dispatchers.Main) {
                    if (transactionResult) {
                        Toast.makeText(
                            requireContext(),
                            "Saldo updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.action_global_homeFragment)
                    } else {
                        Toast.makeText(requireContext(), "Saldo insufficient", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to update saldo: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
