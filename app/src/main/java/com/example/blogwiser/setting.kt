package com.example.blogwiser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class setting : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v:ViewGroup
        v=inflater.inflate(R.layout.fragment_setting, container, false) as ViewGroup
        return v
    }
}