package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class ProfileFragment : Fragment() {
    private lateinit var db: AppDatabase
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    lateinit var tvName: TextView
    lateinit var tvUsername: TextView
    lateinit var tvEmail: TextView
    lateinit var book: ConstraintLayout
    lateinit var edit: ImageButton
    lateinit var logout: ConstraintLayout
    lateinit var tvParticipation: TextView
    lateinit var tvCommunity: TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        tvName = view.findViewById(R.id.profile_name_tv)
//        tvUsername = view.findViewById(R.id.profile_username_tv)
        tvParticipation = view.findViewById(R.id.profile_participation_tv)
        tvCommunity = view.findViewById(R.id.profile_community_tv)
        tvEmail = view.findViewById(R.id.profile_email_tv)
        db = AppDatabase.build(this.requireActivity())
        firestore = FirebaseFirestore.getInstance()
        var users:List<UserEntity>
        var id:String
        coroutine.launch {
            users = db.userDAO().fetch()
            if (users.isNotEmpty()) {
                id = users[0].id
                fetchUserData(id)
            }
        }
        book = view.findViewById(R.id.profile_book)
        book.setOnClickListener{
            findNavController().navigate(R.id.action_global_participationBookFragment)
        }
        edit = view.findViewById(R.id.profile_edit)
        edit.setOnClickListener {
            findNavController().navigate(R.id.action_global_editProfileFragment)
        }
        logout = view.findViewById(R.id.profile_logout)
        logout.setOnClickListener {
            coroutine.launch {
                db.userDAO().deleteAll()
            }
            val intent = Intent(requireActivity(), LandingPageActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun fetchUserData(id: String) {
        try {
            val userDocument = firestore.collection("users").document(id).get().await()
            if (userDocument.exists()) {
                val userData = userDocument.data
                withContext(Dispatchers.Main) {
                    tvName.text = userData?.get("name") as? String
                    tvEmail.text = userData?.get("email") as? String
                }
            }

            val communityQuerySnapshot = firestore.collection("community_member")
                .whereEqualTo("userId", id)
                .get()
                .await()

            val communitySize = communityQuerySnapshot.size()

            val participationQuerySnapshot = firestore.collection("event_participants")
                .whereEqualTo("userId", id)
                .get()
                .await()

            val participationSize = participationQuerySnapshot.size()

            Log.d("TVParticipation",participationSize.toString())
            Log.d("TVCommunity",communitySize.toString())
            withContext(Dispatchers.Main) {
                tvCommunity.text = communitySize.toString()
                tvParticipation.text= participationSize.toString()
//                tvCommunity.text = "3"
//                tvParticipation.text= "3"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}