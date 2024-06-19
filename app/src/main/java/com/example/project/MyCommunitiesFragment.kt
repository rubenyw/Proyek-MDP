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
import com.example.project.databinding.FragmentMyCommunitiesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyCommunitiesFragment : Fragment() {
    private lateinit var binding: FragmentMyCommunitiesBinding;
    val vm:CommunityViewModel by viewModels()

    private lateinit var db: AppDatabase
    private val coroutine = CoroutineScope(Dispatchers.IO)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCommunitiesBinding.inflate(inflater, container, false);
        val rootView = binding.root;
        (requireActivity() as AppCompatActivity).supportActionBar?.hide();
        return rootView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val communityAdapter = MyCommunityAdapter {
            val action =
                MyCommunitiesFragmentDirections.actionMyCommunitiesFragmentToCommunitiesPostsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.rvMyCommunities.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvMyCommunities.adapter = communityAdapter

        vm.userCommunities.observe(viewLifecycleOwner, Observer { userCommunities ->
            communityAdapter.submitList(userCommunities)
        })

        db = AppDatabase.build(this.requireActivity())

        coroutine.launch {
            val users = db.userDAO().fetch()
            if (users.isNotEmpty()) {
                val id = users[0].id
                withContext(Dispatchers.Main) {
                    vm.fetchUserCommunities(id)
                }
            }
        }

        binding.btnFindCommunity.setOnClickListener {
            val action = MyCommunitiesFragmentDirections.actionMyCommunitiesFragmentToFindCommunityFragment()
            findNavController().navigate(action)
        }

        binding.myCommunitiesBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_myCommunitiesFragment_to_homeFragment);
        }
    }
}