package com.example.project

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.project.databinding.FragmentRegisterBinding
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        val rootView = binding.root;
        (requireActivity() as AppCompatActivity).supportActionBar?.hide();
//        db = AppDatabase.getDatabase(requireContext())
//        coroutine = CoroutineScope(Dispatchers.IO);
        return rootView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnRegisterRegister.setOnClickListener{
            var fields = arrayListOf<String>(
                binding.etFullnameRegister.text.toString(),
                binding.etUsernameRegister.text.toString(),
                binding.etEmailRegister.text.toString(),
                binding.etPasswordRegister.text.toString(),
                binding.etConfirmRegister.text.toString(),
            )
            if(Mekanisme.isEmptyField(fields)) {
                Mekanisme.showToast(requireContext(), "Pastikan semua field terisi!");
            }else{
                Mekanisme.checkUsername(fields[1]) { isUsernameAvailable ->
                    if (isUsernameAvailable) {
                        val db = FirebaseFirestore.getInstance()
                        val usersCollection = db.collection("users")
                        val user = hashMapOf(
                            "name" to fields[0],
                            "username" to fields[1],
                            "email" to fields[2],
                            "password" to fields[3],
                            "saldo" to 0,
                            "rank" to 5
                        )
                        usersCollection.add(user)
                            .addOnSuccessListener { documentReference ->
                                Mekanisme.showToast(requireContext(), "User created successfully!");
                                findNavController().navigate(R.id.action_global_loginFragment)
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error adding document", e)
                                Mekanisme.showToast(requireContext(), "Fail to create new user :(");
                            }
                    } else {
                        Mekanisme.showToast(requireContext(), "Username has been taken, Try something else");
                    }
                }
            }
        }
    }
}