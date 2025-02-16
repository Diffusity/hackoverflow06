package com.example.blogwiser

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        val lottie: LottieAnimationView = findViewById(R.id.lottie)
        lottie.playAnimation()
//        var pattern_pass= Pattern.compile("^" +
//                "(?=.*[@#$%^&+=])" +     // at least 1 special character
//                "(?=\\S+$)" +            // no white spaces
//                ".{4,}" +                // at least 4 characters
//                "$")

        emailInput = findViewById(R.id.email)
        passwordInput = findViewById(R.id.password)
        btnLogin = findViewById(R.id.btn)

        auth = FirebaseAuth.getInstance()

        // On Login Button Click
        btnLogin.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showSnackbar("Please fill all fields")
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showSnackbar("Login Successful!")
                    saveUsernameLocally(email)
                    startActivity(Intent(this, home::class.java))
                    finish()
                } else {
                    showSnackbar("Login Failed: ${task.exception?.message}")
                }
            }
    }
    private fun showSnackbar(message:String){
        Snackbar.make(findViewById(R.id.main),message, Snackbar.LENGTH_SHORT).show()
    }

    private fun saveUsernameLocally(email: String) {
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("email", email)
        editor.apply()
    }

}
