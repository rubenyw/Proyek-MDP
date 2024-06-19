package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    lateinit var btnEvent: Button
    lateinit var btnCommunity: Button
    lateinit var tvName: TextView
    lateinit var btnTopup: ImageButton
    lateinit var btnWithdraw: ImageButton
    lateinit var btnHistory: ImageButton
    lateinit var tvSaldo: TextView
    lateinit var btnProfile :ImageButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the database and Firestore
        db = AppDatabase.build(this.requireActivity())
        firestore = FirebaseFirestore.getInstance()

        tvName = view.findViewById(R.id.home_username_tv)
        coroutine.launch {
            val users = db.userDAO().fetch()
            if (users.isNotEmpty()) {
                val name = users[0].name
                userId = users[0].id
                fetchUserData(userId)
                withContext(Dispatchers.Main) {
                    tvName.text = name
                }
            }
        }

        btnEvent = view.findViewById(R.id.btnEvent)
        btnEvent.setOnClickListener {
            findNavController().navigate(R.id.action_global_upcomingEventsFragment)
        }
        btnCommunity = view.findViewById(R.id.btnCommunity)
        btnCommunity.setOnClickListener {
            findNavController().navigate(R.id.action_global_myCommunitiesFragment)
        }
        btnTopup = view.findViewById(R.id.home_topup_btn)
        btnWithdraw = view.findViewById(R.id.home_withdraw_btn)
        btnHistory = view.findViewById(R.id.home_history_btn)
        btnTopup.setOnClickListener {
            findNavController().navigate(R.id.action_global_donateFragment)
        }
        btnWithdraw.setOnClickListener {
            findNavController().navigate(R.id.action_global_withdrawFragment)
        }
        btnHistory.setOnClickListener {
            findNavController().navigate(R.id.action_global_historyFragment)
        }
        tvSaldo = view.findViewById(R.id.home_saldo_tv)
        btnProfile = view.findViewById(R.id.home_profile_btn)
        btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_global_profileFragment)
        }
    }

    private suspend fun fetchUserData(id: String) {
        try {
            val documentSnapshot = firestore.collection("users").document(id).get().await()
            if (documentSnapshot.exists()) {
                val userData = documentSnapshot.data
                withContext(Dispatchers.Main) {
                    val saldo = userData?.get("saldo") as? Double
                    tvSaldo.text = "Rp ${saldo?.toInt() ?: 0}"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
