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

        val lottie: LottieAnimationView = findViewById(R.id.lottie)
        lottie.playAnimation()

        emailInput = findViewById(R.id.email)
        passwordInput = findViewById(R.id.password)
        btnLogin = findViewById(R.id.btn)

        auth = FirebaseAuth.getInstance()

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

                    val intent = Intent(this, home::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
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
