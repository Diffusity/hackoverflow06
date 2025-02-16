package com.example.blogwiser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*

class blolg_details : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_blolg_details)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        val tvTitle = findViewById<TextView>(R.id.title)
        val tvDescription = findViewById<TextView>(R.id.description)
        val btnDelete = findViewById<MaterialButton>(R.id.btnDelete)

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val myuser = intent.getBooleanExtra("Myuser", false)

        btnDelete.visibility = if (myuser) View.VISIBLE else View.GONE
        tvTitle.text = title
        tvDescription.text = description

        // Handle Delete Button Click
        btnDelete.setOnClickListener {
            getUsernameAndDeleteBlog(title)
        }
    }

    private fun getUsernameAndDeleteBlog(blogTitle: String?) {
        if (blogTitle.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Blog title not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Get email from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)

        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Email not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Find username using email
        database.child("Users").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val username = userSnapshot.child("username").getValue(String::class.java)
                            if (!username.isNullOrEmpty()) {
                                findBlogIdAndDelete(username, blogTitle)
                            } else {
                                Toast.makeText(this@blolg_details, "Username not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@blolg_details, "No user found with this email", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@blolg_details, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun findBlogIdAndDelete(username: String, blogTitle: String) {
        // Search for the blog ID under Blog > username
        database.child("Blog").child(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (blogSnapshot in snapshot.children) {
                            val blogId = blogSnapshot.key // Get the blog ID
                            val storedTitle = blogSnapshot.child("title").getValue(String::class.java)

                            // Ensure the blog title matches
                            if (storedTitle == blogTitle) {
                                deleteBlog(username, blogId!!)
                                return
                            }
                        }
                    } else {
                        Toast.makeText(this@blolg_details, "Blog not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@blolg_details, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun deleteBlog(username: String, blogId: String) {
        // Delete the blog from Blog > username > blogId
        database.child("Blog").child(username).child(blogId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Blog deleted successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, myblogs::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete blog", Toast.LENGTH_SHORT).show()
            }
    }
}
