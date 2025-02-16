package com.example.blogwiser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton

class setting : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_setting, container, false)

        val logout: MaterialButton = v.findViewById(R.id.logout)
        val myblog: MaterialButton = v.findViewById(R.id.myblogs)

        // Logout button click
        logout.setOnClickListener {
            logoutUser()
        }

        // My Blogs button click
        myblog.setOnClickListener {
            val intent = Intent(requireContext(), myblogs::class.java)
            startActivity(intent)
        }

        return v
    }

    private fun logoutUser() {
        // Clear SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // Navigate to Login Activity & Clear All Previous Activities
        val intent = Intent(requireContext(), login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        requireActivity().finish()  // Close current activity
    }
}
