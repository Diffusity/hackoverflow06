package com.example.blogwiser

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.blogwiser.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        val lottie: LottieAnimationView

        lottie = findViewById(R.id.lottie)
        lottie.playAnimation()

        val usernameInput: EditText = findViewById(R.id.username)
        val emailInput: EditText = findViewById(R.id.email)
        val passwordInput: EditText = findViewById(R.id.password)
        val btn: Button = findViewById(R.id.btn)



        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Users")

        btn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkUserExists(username, email, password)        }
    }

    private fun checkUserExists(username: String, email: String, password: String) {
        databaseRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Username already exists
                    Toast.makeText(applicationContext, "Username already taken!", Toast.LENGTH_SHORT).show()
                } else {
                    // Check if email exists
                    databaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(emailSnapshot: DataSnapshot) {
                            if (emailSnapshot.exists()) {
                                Toast.makeText(applicationContext, "Email already in use!", Toast.LENGTH_SHORT).show()
                            } else {
                                // Proceed to sign up the user
                                signUpUser(username, email, password)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
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

                        // Store user data in Firebase Database
                        databaseRef.child(userId).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()
                                    finish()  // Close signup screen
                                } else {
                                    Toast.makeText(this, "Failed to store user data: ${dbTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "User ID is null. Authentication failed.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Signup Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}


