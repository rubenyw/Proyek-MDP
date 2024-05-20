package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.project.databinding.FragmentLandingPageBinding

class LandingPageFragment : Fragment() {
    private lateinit var binding: FragmentLandingPageBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLandingPageBinding.inflate(inflater, container, false);
        val rootView = binding.root;
        (requireActivity() as AppCompatActivity).supportActionBar?.hide();
//        db = AppDatabase.getDatabase(requireContext())
//        coroutine = CoroutineScope(Dispatchers.IO);
        return rootView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLoginStart.setOnClickListener{
            findNavController().navigate(R.id.action_landingPageFragment_to_loginFragment);
        }

        binding.btnRegisterStart.setOnClickListener{
            findNavController().navigate(R.id.action_landingPageFragment_to_registerFragment);
        }
    }
}