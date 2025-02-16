package com.example.blogwiser

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blogwiser.adapters.BlogAdapter
import com.example.blogwiser.adapters.MyAdapter
import com.example.blogwiser.model.Blog

import com.google.firebase.database.*

class myblogs : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var blogAdapter: MyAdapter
    private val blogList = mutableListOf<Blog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_myblogs)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recylerviewBlog)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load Blogs from Firebase
        loadBlogs()
    }

    private fun loadBlogs() {
        val email = sharedPreferences.getString("email", null)

        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "No email found", Toast.LENGTH_SHORT).show()
            return
        }

        // Find userId using email
        database.child("Users").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val username = userSnapshot.child("username").value.toString()

                            // Load blogs under this username
                            database.child("Blog").child(username)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(blogSnapshot: DataSnapshot) {
                                        blogList.clear()
                                        for (blog in blogSnapshot.children) {
                                            val blogData = blog.getValue(Blog::class.java)
                                            if (blogData != null) {
                                                blogList.add(blogData)
                                            }
                                        }
                                        blogAdapter = MyAdapter(this@myblogs, blogList)
                                        recyclerView.adapter = blogAdapter
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(applicationContext, "Failed to load blogs", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                    } else {
                        Toast.makeText(applicationContext, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
