package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.project.databinding.FragmentRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding;
    private lateinit var db: AppDatabase;
    private lateinit var coroutine: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        val rootView = binding.root;
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        db = AppDatabase.getDatabase(requireContext())
        coroutine = CoroutineScope(Dispatchers.IO);
        return rootView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        binding.registerLoginBtn.setOnClickListener(){
//            findNavController().navigate(R.id.action_registerFragment_to_loginFragment);
        }

        binding.registerRegisterBtn.setOnClickListener(){
            var field = arrayListOf<String>(
                binding.registerNameTv.text.toString(),
                binding.registerUsernameTv.text.toString(),
                binding.registerPasswordTv.text.toString(),
                binding.registerConfirmTv.text.toString(),
            );

            if(Mekanisme.isEmptyField(field)){
                Mekanisme.showToast(requireContext(), "Ada field kosong!");
            }else if (field[2] != field[3]){
                Mekanisme.showToast(requireContext(), "Password dan Cofirm Password Tidak Sama!");
            }else{
                coroutine.launch {
//                    var user = db.userDao().get(field[1]);
//                    if(user != null){
//                        requireActivity().runOnUiThread {
//                            Mekanisme.showToast(requireContext(), "Username telah terpakai!");
//                        }
//                    }else{
//                        var newUser = UserEntity(
//                            name = field[0],
//                            username = field[1],
//                            password = field[2],
//                        )
//                        db.userDao().insert(newUser);
//                        requireActivity().runOnUiThread {
//                            Mekanisme.showToast(requireContext(), "User Registered!");
//                            binding.registerNameTv.setText("");
//                            binding.registerUsernameTv.setText("");
//                            binding.registerPasswordTv.setText("");
//                            binding.registerConfirmTv.setText("");
//                        }
//                    }
                }

            }
        }
    }
}