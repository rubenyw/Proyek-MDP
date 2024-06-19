package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.databinding.FragmentCommunitiesPostsBinding
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommunityPostFragment : Fragment() {
    private lateinit var binding: FragmentCommunitiesPostsBinding;
    val vm:CommunityViewModel by viewModels()
    val navArgs:CommunityPostFragmentArgs by navArgs()
    private lateinit var db: AppDatabase
    private lateinit var firestore: FirebaseFirestore
    private val coroutine = CoroutineScope(Dispatchers.IO)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunitiesPostsBinding.inflate(inflater, container, false);
        val rootView = binding.root;
        (requireActivity() as AppCompatActivity).supportActionBar?.hide();
        return rootView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val communityPostAdapter = CommunityPostAdapter()

        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPosts.adapter = communityPostAdapter

        val id = navArgs.currentCommunityId
        vm.fetchCommunityPosts(id)
        vm.communityPosts.observe(viewLifecycleOwner, Observer { posts ->
            communityPostAdapter.submitList(posts)
        })

        firestore = FirebaseFirestore.getInstance()

        coroutine.launch {
            try {
                val snapshot = firestore.collection("communities").document(id).get().await()
                if (snapshot.exists()) {
                    val community = snapshot.toObject(CommunityEntity::class.java)
                    withContext(Dispatchers.Main) {
                        binding.postCommunityTitleTv.text = community?.name
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Handle the error, e.g., show a toast
                    Mekanisme.showToast(requireContext(), "Error fetching community");
                }
            }
        }

        binding.btnSendPost.setOnClickListener {
            db = AppDatabase.build(this.requireActivity())

            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val context = binding.tvNewPost.text.toString()
            coroutine.launch {
                val users = db.userDAO().fetch()
                if (users.isNotEmpty()) {
                    val name = users[0].name
                    withContext(Dispatchers.Main) {
                        vm.createCommunityPost(id, name, currentDate, context)
                        binding.tvNewPost.setText("")
                    }
                }
            }
        }

        binding.postCommunityBackBtn.setOnClickListener{
            findNavController().navigate(R.id.action_global_myCommunitiesFragment);
        }
    }
}