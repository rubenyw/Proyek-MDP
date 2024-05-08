package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController

class start : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    lateinit var btnLogin:Button
    lateinit var btnRegister:Button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnLogin = view.findViewById(R.id.btnLogin_start)
        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_global_login)
        }
        btnRegister = view.findViewById(R.id.btnRegister_start)
        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_global_register)
        }
    }
}