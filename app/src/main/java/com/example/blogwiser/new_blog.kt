package com.example.blogwiser

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class new_blog : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnUpload: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_blog, container, false)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Initialize Views
        etTitle = view.findViewById(R.id.title)
        etDescription = view.findViewById(R.id.description)
        btnUpload = view.findViewById(R.id.btn)

        // Set Button Click Listener
        btnUpload.setOnClickListener {
            fetchUserIdAndUploadBlog()
        }

        return view
    }

    private fun fetchUserIdAndUploadBlog() {
        val email = sharedPreferences.getString("email", null)

        if (email.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No email found in SharedPreferences", Toast.LENGTH_SHORT).show()
            return
        }

        // Query Firebase to find userId based on email
        database.child("Users").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val userId = userSnapshot.key  // userId key
                            val username = userSnapshot.child("username").value.toString()

                            if (userId != null) {
                                uploadBlog(username)
                                return
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun uploadBlog(username: String) {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter both title and description", Toast.LENGTH_SHORT).show()
            return
        }

        val blogId = database.child("Blog").child(username).push().key!!

        val blogData = mapOf(
            "blogId" to blogId,
            "title" to title,
            "description" to description
        )

        database.child("Blog").child(username).child(blogId).setValue(blogData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Blog uploaded successfully!", Toast.LENGTH_SHORT).show()
                etTitle.text.clear()
                etDescription.text.clear()
                val transaction: FragmentTransaction =childFragmentManager.beginTransaction()
                transaction.replace(R.id.frame1,blog(),null).commit()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to upload blog", Toast.LENGTH_SHORT).show()
            }
    }
}
