package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.project.databinding.FragmentLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding;
    private lateinit var db: AppDatabase;
    private lateinit var coroutine: CoroutineScope
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        val rootView = binding.root;
        (requireActivity() as AppCompatActivity).supportActionBar?.hide();
        db = AppDatabase.getDatabase(requireContext())
        coroutine = CoroutineScope(Dispatchers.IO);
        return rootView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        binding.loginRegisterBtn.setOnClickListener() {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment);
        }

        binding.loginLoginBtn.setOnClickListener() {
            var field = arrayListOf<String>(
                binding.loginUsernameTv.text.toString(),
                binding.loginPasswordTv.text.toString(),
            )

            if (Mekanisme.isEmptyField(field)) {
                Mekanisme.showToast(requireContext(), "Terdapat Field Kosong!");
            } else {

            }
        }
    }
}