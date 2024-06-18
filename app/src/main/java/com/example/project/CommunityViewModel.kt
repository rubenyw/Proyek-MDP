package com.example.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.OnCompleteListener

class CommunityViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val communitiesCollection = db.collection("communities")

    private val _communities = MutableLiveData<List<CommunityEntity>>()
    val communities: LiveData<List<CommunityEntity>> get() = _communities

    fun fetchCommunities() {
        communitiesCollection.get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    val communitiesList = mutableListOf<CommunityEntity>()
                    for (document in task.result) {
                        val id = document.id
                        val name = document.getString("name") ?: ""
                        val community = CommunityEntity(id, name)
                        communitiesList.add(community)
                    }
                    _communities.value = communitiesList
                } else {
                    println("Error getting documents: ${task.exception}")
                }
            })
    }
}
