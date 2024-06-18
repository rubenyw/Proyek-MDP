package com.example.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

class CommunityViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val communitiesCollection = db.collection("communities")
    private val communitiesMemberCollection = db.collection("community_member")
    private val communitiesPost = db.collection("community_posts")

    private val _communities = MutableLiveData<List<CommunityEntity>>()
    val communities: LiveData<List<CommunityEntity>> get() = _communities

    private val _userCommunities = MutableLiveData<List<CommunityEntity>>()
    val userCommunities: LiveData<List<CommunityEntity>> get() = _userCommunities

    private val _communityPosts = MutableLiveData<List<PostEntity>>()
    val communityPosts: LiveData<List<PostEntity>> get() = _communityPosts

    fun addCommunityMember(communityId: String, userId: String) {
        viewModelScope.launch {
            try {
                val newMember = hashMapOf(
                    "communityId" to communityId,
                    "userId" to userId
                )
                withContext(Dispatchers.IO) {
                    communitiesMemberCollection.add(newMember).await()
                }
                println("Member successfully added: $userId to community $communityId")
                fetchCommunities(userId)
            } catch (e: Exception) {
                println("Error adding member to community: ${e.message}")
            }
        }
    }
    fun fetchCommunities(userId: String) {
        viewModelScope.launch {
            try {
                val joinedCommunitiesSnapshot = withContext(Dispatchers.IO) {
                    communitiesMemberCollection
                        .whereEqualTo("userId", userId)
                        .get()
                        .await()
                }
                val joinedCommunityIds = joinedCommunitiesSnapshot.documents.map { it.getString("communityId") }.filterNotNull()
                val allCommunitiesSnapshot = withContext(Dispatchers.IO) {
                    communitiesCollection.get().await()
                }
                val allCommunitiesList = allCommunitiesSnapshot.documents.map { document ->
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    CommunityEntity(id, name)
                }
                val notJoinedCommunitiesList = allCommunitiesList.filterNot { community ->
                    joinedCommunityIds.contains(community.id)
                }
                _communities.postValue(notJoinedCommunitiesList)
            } catch (e: Exception) {
                println("Error getting documents: ${e.message}")
            }
        }
    }

    fun fetchUserCommunities(userId: String) {
        viewModelScope.launch {
            try {
                val membershipSnapshot = withContext(Dispatchers.IO) {
                    communitiesMemberCollection.whereEqualTo("userId", userId).get().await()
                }
                val communityIds = membershipSnapshot.documents.mapNotNull { it.getString("communityId") }

                if (communityIds.isNotEmpty()) {
                    val communitiesSnapshot = withContext(Dispatchers.IO) {
                        communitiesCollection.whereIn(FieldPath.documentId(), communityIds).get().await()
                    }
                    val userCommunitiesList = communitiesSnapshot.documents.map { document ->
                        val id = document.id
                        val name = document.getString("name") ?: ""
                        CommunityEntity(id, name)
                    }
                    _userCommunities.postValue(userCommunitiesList)
                } else {
                    _userCommunities.postValue(emptyList())
                }
            } catch (e: Exception) {
                println("Error getting community memberships or communities: ${e.message}")
            }
        }
    }

    fun fetchCommunityPosts(communityId: String) {
        viewModelScope.launch {
            try {
                val postsSnapshot = withContext(Dispatchers.IO) {
                    communitiesPost.whereEqualTo("communityId", communityId).get().await()
                }
                val postsList = postsSnapshot.documents.map { document ->
                    val id = document.id
                    val owner = document.getString("owner") ?: ""
                    val date = document.getString("date") ?: ""
                    val context = document.getString("context") ?: ""
                    PostEntity(id, owner, date, context)
                }
                _communityPosts.postValue(postsList)
            } catch (e: Exception) {
                println("Error fetching community posts: ${e.message}")
                _communityPosts.postValue(emptyList())
            }
        }
    }

    fun createCommunityPost(communityId: String, ownerName: String, date: String, context: String) {
        viewModelScope.launch {
            try {
                val newPost = hashMapOf(
                    "communityId" to communityId,
                    "owner" to ownerName,
                    "date" to date,
                    "context" to context
                )
                withContext(Dispatchers.IO) {
                    communitiesPost.add(newPost).await()
                }
                println("New post successfully created by $ownerName in community $communityId")
                fetchCommunityPosts(communityId)
            } catch (e: Exception) {
                println("Error creating new post: ${e.message}")
            }
        }
    }

}