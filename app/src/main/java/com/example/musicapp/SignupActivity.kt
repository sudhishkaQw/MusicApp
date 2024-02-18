package com.example.musicapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.musicapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.createButton.setOnClickListener {
            val email=binding.emailAddress.text.toString()
            val pass =binding.password.text.toString()
            val conPass = binding.confirmPassword.text.toString()

            if(!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(),email))
            {
                binding.emailAddress.setError("Invalid email")
                return@setOnClickListener
            }
            if(pass.length<6)
            {
                binding.password.setError("Password should be atleast 6 char")
                return@setOnClickListener
            }
            if(!pass.equals(conPass))
            {
                binding.confirmPassword.setError("Password doesn't matches")
                return@setOnClickListener
            }
            createAccountWithFirebase(email,pass)
        }
        binding.already.setOnClickListener {
            startActivity(Intent(this@SignupActivity,LoginActivity::class.java))
        }
    }

    private fun createAccountWithFirebase(email: String, pass: String) {
        setProgress(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass)
            .addOnSuccessListener {
                setProgress(false)
                Toast.makeText(applicationContext,"User created successful",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                setProgress(false)
                Toast.makeText(applicationContext,"Create account failed",Toast.LENGTH_SHORT).show()
            }
    }
    private fun setProgress(inProgress : Boolean)
    {
        if(inProgress)
        {
            binding.createButton.visibility=View.GONE
            binding.progressBar.visibility=View.VISIBLE
        }
        else
        {
            binding.createButton.visibility=View.VISIBLE
            binding.progressBar.visibility=View.GONE
        }
    }
}