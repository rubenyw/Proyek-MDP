package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.project.databinding.FragmentMyCommunitiesBinding

class MyCommunitiesFragment : Fragment() {
    private lateinit var binding: FragmentMyCommunitiesBinding;

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
        binding.btnFindCommunity.setOnClickListener {
            val action = MyCommunitiesFragmentDirections.actionMyCommunitiesFragmentToFindCommunityFragment()
            findNavController().navigate(action)
        }
    }
}