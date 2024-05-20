package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.project.databinding.FragmentLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        val rootView = binding.root;
        (requireActivity() as AppCompatActivity).supportActionBar?.hide();
//        db = AppDatabase.getDatabase(requireContext())
//        coroutine = CoroutineScope(Dispatchers.IO);
        return rootView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLoginLogin.setOnClickListener {
            var field = arrayListOf<String>(
                binding.etUsernameLogin.text.toString(),
                binding.etPasswordLogin.text.toString(),
            );

            if(Mekanisme.isEmptyField(field)){
                Mekanisme.showToast(requireContext(), "Pastikan semua field terisi!");
            }else{
                Mekanisme.authenticate(field[0], field[1]) { isValid, documentId ->
                    if(isValid){
                        Mekanisme.showToast(requireContext(), "TEST BREHASIL")
                    }else{
                        Mekanisme.showToast(requireContext(), "Couldn't found your account, Recheck your username and password!");
                    }
                }
            }
        }
    }
}