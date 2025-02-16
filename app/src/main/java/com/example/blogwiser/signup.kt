package com.example.blogwiser

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.blogwiser.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import java.util.regex.Pattern

class signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val lottie: LottieAnimationView = findViewById(R.id.lottie)
        lottie.playAnimation()

        val usernameInput: EditText = findViewById(R.id.username)
        val emailInput: EditText = findViewById(R.id.email)
        val passwordInput: EditText = findViewById(R.id.password)
        val btn: Button = findViewById(R.id.btn)

        val passwordPattern = Pattern.compile(
            "^(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[@#$%^&+=])" +
                    "(?=\\S+$)" +
                    ".{8,}$"
        )

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Users")

        btn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showSnackbar("Please fill all fields")
                return@setOnClickListener
            }

            // ✅ Validate password using regex
            if (!passwordPattern.matcher(password).matches()) {
                showSnackbar("Password must be 8+ characters with uppercase, lowercase, number & special character")
                return@setOnClickListener
            }

            checkUserExists(username, email, password)
        }
    }

    private fun checkUserExists(username: String, email: String, password: String) {
        databaseRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    showSnackbar("Username already taken!")
                } else {
                    databaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(emailSnapshot: DataSnapshot) {
                            if (emailSnapshot.exists()) {
                                showSnackbar("Email already in use!")
                            } else {
                                signUpUser(username, email, password)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            showSnackbar("Database error: ${error.message}")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showSnackbar("Database error: ${error.message}")
            }
        })
    }

    private fun signUpUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val user = User(username, email, "")

                        databaseRef.child(userId).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    showSnackbar("Signup Successful!")
                                    saveUsernameLocally(email)
                                    startActivity(Intent(this, home::class.java))
                                } else {
                                    showSnackbar("Failed to store user data: ${dbTask.exception?.message}")
                                }
                            }
                    } else {
                        showSnackbar("User ID is null. Authentication failed.")
                    }
                } else {
                    showSnackbar("Signup Failed: ${task.exception?.message}")
                }
            }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun saveUsernameLocally(email: String) {
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("email", email)
        editor.apply()
    }

}
