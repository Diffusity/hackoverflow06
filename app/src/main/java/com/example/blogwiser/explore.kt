package com.example.blogwiser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blogwiser.adapters.BlogAdapter
import com.example.blogwiser.model.Blog
import com.google.firebase.database.*
import kotlin.random.Random

class explore : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var blogAdapter: BlogAdapter
    private val blogList = mutableListOf<Blog>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("Blog")

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recylerviewBlog)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Adapter
        blogAdapter = BlogAdapter(requireContext(), blogList)
        recyclerView.adapter = blogAdapter

        // Load all blogs randomly
        fetchAllBlogsRandomly()

        return view
    }

    private fun fetchAllBlogsRandomly() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    blogList.clear()

                    for (userSnapshot in snapshot.children) { // Loop through all users
                        for (blogSnapshot in userSnapshot.children) { // Loop through all blogs of each user
                            val blogData = blogSnapshot.getValue(Blog::class.java)
                            if (blogData != null) {
                                blogList.add(blogData)
                            }
                        }
                    }

                    // Shuffle the list to display blogs in random order
                    blogList.shuffle()

                    // Notify adapter after fetching data
                    blogAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "No blogs found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load blogs", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
