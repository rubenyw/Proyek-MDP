package com.example.project

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.databinding.FragmentFindCommunityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class FindCommunityFragment : Fragment() {
    private lateinit var binding: FragmentFindCommunityBinding;
    val vm:CommunityViewModel by viewModels()
    private lateinit var db: AppDatabase
    private val coroutine = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindCommunityBinding.inflate(inflater, container, false);
        val rootView = binding.root;
        (requireActivity() as AppCompatActivity).supportActionBar?.hide();
        return rootView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.build(this.requireActivity())

        coroutine.launch {
            val users = db.userDAO().fetch()
            if (users.isNotEmpty()) {
                val id = users[0].id
                withContext(Dispatchers.Main) {
                    vm.fetchCommunities(id)
                }
            }
        }
        val communityAdapter = FindCommunityAdapter{
            coroutine.launch {
                val users = db.userDAO().fetch()
                if (users.isNotEmpty()) {
                    val id = users[0].id
                    withContext(Dispatchers.Main) {
                        vm.addCommunityMember(it, id)
                    }
                }
            }
        }
        binding.rvFindCommunities.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvFindCommunities.adapter = communityAdapter

        vm.communities.observe(viewLifecycleOwner, Observer { communities ->
            communityAdapter.submitList(communities)
        })

        binding.tvSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim().lowercase(Locale.getDefault())
                val filteredCommunities = vm.communities.value?.filter { community ->
                    community.name.lowercase(Locale.getDefault()).contains(searchText)
                }
                communityAdapter.submitList(filteredCommunities)
            }
        })
    }
}