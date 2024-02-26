package com.example.musicapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musicapp.databinding.ActivityProfileBinding
import com.example.musicapp.model.UserModel
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private val userFetcher = UserFetcher()
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchUserDetails()
        val newCreditScore = intent.getLongExtra("NEW_CREDIT_SCORE", -1)
        if (newCreditScore != -1L) {
            // Call updateUserCreditScore using coroutines
            updateUserCreditScore(newCreditScore)
        }
        binding.update.setOnClickListener {
            // Get updated username and credit score from EditText fields
            binding.update.setOnClickListener {
                val updatedUsername = binding.profileUsername.text.toString()

                if (updatedUsername.isNotEmpty()) {
                    val updatedUserData = mapOf(
                        "username" to updatedUsername
                    )

                    // Call updateUserDetails using coroutines
                    updateUserDetails(updatedUserData)
                } else {
                    Toast.makeText(this, "Please enter a valid username", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


    private fun updateUserCreditScore(newCreditScore: Long) {
        // Call updateCreditScore using coroutines
        lifecycleScope.launch {
            try {
                userFetcher.updateCreditScore(
                    newCreditScore,
                    onSuccess = {
                        // Show success message
                        Toast.makeText(this@ProfileActivity, "Credit score updated successfully", Toast.LENGTH_SHORT).show()

                        // Optionally, perform any additional UI updates here
                    },
                    onFailure = { e ->
                        // Handle error
                        Log.e(TAG, "Error updating credit score", e)
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error updating credit score", e)
                // Handle error
            }
        }
    }



    private fun fetchUserDetails() {
        lifecycleScope.launch {
            try {
                val userData = userFetcher.getUserDetails()
                if (userData != null) {
                    // User details fetched successfully, update UI
                    updateUI(userData)
                } else {
                    // User details not found or null, handle accordingly
                    Log.e(TAG, "Error: User details not found")
                }
            } catch (e: Exception) {
                // Handle exception, such as displaying an error message
                Log.e(TAG, "Error fetching user details", e)
            }
        }
    }




    private fun updateUI(userData: UserModel?) {
        if (userData != null) {
            // Check if the necessary keys exist in the userData
            if (userData.username.isNotEmpty() && userData.creditScore != null) {
                // Update UI elements with user data
                binding.profileUsername.setText(userData.username)
                binding.profileCreditScore.setText(userData.creditScore.toString())
            } else {
                Log.e(TAG, "Error: Missing keys in userData")
            }
        } else {
            Log.e(TAG, "Error: userData is null")
        }
    }


    private fun updateUserDetails(updatedUserData: Map<String, Any>) {
        lifecycleScope.launch {
            try {
                userFetcher.updateUserDetails(updatedUserData)
                // Handle success, such as showing a success message
                Toast.makeText(this@ProfileActivity, "Username updated successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error updating username", e)
                // Handle failure, such as displaying an error message
                Toast.makeText(this@ProfileActivity, "Failed to update username", Toast.LENGTH_SHORT).show()
            }
        }
    }




    companion object {
        private const val TAG = "UserProfile"
    }

    }
