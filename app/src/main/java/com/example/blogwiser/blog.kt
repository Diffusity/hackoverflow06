package com.example.blogwiser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.button.MaterialButton

class blog : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val btn:MaterialButton
        val v:ViewGroup
        v=inflater.inflate(R.layout.fragment_blog, container, false) as ViewGroup
        btn=v.findViewById(R.id.btn)
        btn.setOnClickListener(View.OnClickListener {
            val transaction:FragmentTransaction=childFragmentManager.beginTransaction()
            transaction.replace(R.id.frame,new_blog(),null).commit()
        })
        return v
    }
}