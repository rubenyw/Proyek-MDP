package com.example.project

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.databinding.FragmentHistoryBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var db: AppDatabase
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val rootView = binding.root
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.build(requireActivity())
        firestore = FirebaseFirestore.getInstance()
        userId = ""

        coroutine.launch {
            try {
                val users = db.userDAO().fetch()
                if (users.isNotEmpty()) {
                    userId = users[0].id
                }

                val donationsRef = firestore.collection("donationHistory")
                    .whereEqualTo("userId", userId)

                val snapshot = donationsRef.get().await()

                if (snapshot.isEmpty) {
                    // Handle case where no donation history is found for the user
                    Log.d(TAG, "No donation history found for userId: $userId")
                    return@launch
                }

                val donations = snapshot.toObjects(DonationHistoryEntity::class.java)
                withContext(Dispatchers.Main) {
                    val historyAdapter = HistoryAdapter(donations)
                    binding.rvDonation.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.rvDonation.adapter = historyAdapter
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching donation history", e)
                withContext(Dispatchers.Main) {
                    Mekanisme.showToast(requireContext(), "Error fetching donation history");
                }
            }
        }

        binding.historyBackBtn.setOnClickListener{
            findNavController().navigate(R.id.action_historyFragment_to_homeFragment)
        }
    }
    companion object {
        private const val TAG = "HistoryFragment"
    }
}
