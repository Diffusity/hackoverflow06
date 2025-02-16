package com.example.blogwiser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class explore : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: ViewGroup
        v= inflater.inflate(R.layout.fragment_explore, container, false) as ViewGroup
        // Inflate the layout for this fragment
        return v
    }
}