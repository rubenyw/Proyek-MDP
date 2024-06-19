package com.example.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import android.util.Log

// Create the logging interceptor
val logging = HttpLoggingInterceptor().apply {
    setLevel(HttpLoggingInterceptor.Level.BODY)
}

// Create the OkHttpClient and add the interceptor
val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

// Create Moshi instance with KotlinJsonAdapterFactory
val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Build the Retrofit instance
private val retrofit = Retrofit.Builder()
    .baseUrl("https://v2.jokeapi.dev/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(client)
    .build()

private val jokeApiService = retrofit.create(JokeApiService::class.java)

class DonateFragment : Fragment() {
    private lateinit var db: AppDatabase
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_donate, container, false)
    }

    lateinit var etAmmount: EditText
    lateinit var btnConfirm: Button
    lateinit var btnBack: ImageButton
    lateinit var tvQuote: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.build(this.requireActivity())
        firestore = FirebaseFirestore.getInstance()

        coroutine.launch {
            val users = db.userDAO().fetch()
            if (users.isNotEmpty()) {
                userId = users[0].id
            }
        }

        btnConfirm = view.findViewById(R.id.donate_topup_btn)
        etAmmount = view.findViewById(R.id.donate_ammount_et)
        btnBack = view.findViewById(R.id.donate_back_btn)
        tvQuote = view.findViewById(R.id.donate_quote_tv)

        coroutine.launch {
            try {
                val response = jokeApiService.getJoke()
                Log.e("Joke",response.toString())
                withContext(Dispatchers.Main) {
                    tvQuote.text = response.joke ?: "No joke found"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvQuote.text = "Failed to load joke"
                    Log.e("JokeApiError", "Error loading joke", e)
                }
            }
        }

        btnConfirm.setOnClickListener {
            if (etAmmount.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Pastikan jumlah terisi!", Toast.LENGTH_SHORT).show()
            } else {
                val amount = etAmmount.text.toString().toDouble()
                updateSaldo(userId, amount)
            }
        }

        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_global_homeFragment)
        }
    }

    private fun updateSaldo(userId: String, amount: Double) {
        coroutine.launch {
            try {
                val userRef = firestore.collection("users").document(userId)
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val newSaldo = snapshot.getDouble("saldo")!! + amount
                    transaction.update(userRef, "saldo", newSaldo)
                }.await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Saldo updated successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_global_homeFragment)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to update saldo: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
