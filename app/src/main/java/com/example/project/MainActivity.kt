package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import androidx.navigation.findNavController
import com.example.project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        supportActionBar?.hide();

        /*
        binding.bottomNavigation.setOnItemSelectedListener {item ->
            when(item.itemId){
                R.id.home_botnav -> {
                    findNavController(binding.fragmentContainerView.id).navigate(R.id.action_global_homeFragment);
                    true
                }
                R.id.prof_botnav -> {
                    findNavController(binding.fragmentContainerView.id).navigate(R.id.action_global_profileFragment);
                    true
                }
                else -> false
            }
        }
        */
    }
}