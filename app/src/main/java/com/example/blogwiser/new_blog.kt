package com.example.blogwiser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

class new_blog : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v:ViewGroup
        val title:EditText
        v=inflater.inflate(R.layout.fragment_new_blog, container, false) as ViewGroup
        // Inflate the layout for this fragment
        title=v.findViewById(R.id.title)
        return v
    }
}