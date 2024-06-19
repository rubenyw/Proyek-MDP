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

class DonateFragment : Fragment() {
    private lateinit var db: AppDatabase
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_donate, container, false)
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

        btnConfirm = view.findViewById(R.id.donate_topup_btn)
        etAmmount = view.findViewById(R.id.donate_ammount_et)

        btnConfirm.setOnClickListener {
            if (etAmmount.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Pastikan jumlah terisi!", Toast.LENGTH_SHORT).show()
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
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val newSaldo = snapshot.getDouble("saldo")!! + amount
                    transaction.update(userRef, "saldo", newSaldo)
                }.await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Saldo updated successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_global_homeFragment)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to update saldo: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
