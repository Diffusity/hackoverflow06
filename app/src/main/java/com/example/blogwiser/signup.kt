package com.example.blogwiser

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        val lottie: LottieAnimationView

        lottie = findViewById(R.id.lottie)
        lottie.playAnimation()

        val usernameInput: EditText = findViewById(R.id.username)
        val emailInput: EditText = findViewById(R.id.email)
        val passwordInput: EditText = findViewById(R.id.password)
        val btn: Button = findViewById(R.id.btn)
        var pattern_pass= Pattern.compile("^" +
                "(?=.*[@#$%^&+=])" +     // at least 1 special character
                "(?=\\S+$)" +            // no white spaces
                ".{4,}" +                // at least 4 characters
                "$")


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
            if(!password.isEmpty() && pattern_pass.matcher(password).matches()){
                showSnackbar("Please enter valid password")
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
                    showSnackbar("Username already taken!")
                } else {
                    // Check if email exists
                    databaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(emailSnapshot: DataSnapshot) {
                            if (emailSnapshot.exists()) {
                                showSnackbar("Email already in use!")
                            } else {
                                // Proceed to sign up the user
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

                        // Store user data in Firebase Database
                        databaseRef.child(userId).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    showSnackbar("Signup Successful!")
                                    finish()  // Close signup screen
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

    private fun showSnackbar(message:String){
        Snackbar.make(findViewById(R.id.main),message,Snackbar.LENGTH_SHORT).show()
    }
}


