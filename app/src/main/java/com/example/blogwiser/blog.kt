package com.example.blogwiser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
class blog : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v:ViewGroup
        v=inflater.inflate(R.layout.fragment_blog, container, false) as ViewGroup
        return v
    }
}