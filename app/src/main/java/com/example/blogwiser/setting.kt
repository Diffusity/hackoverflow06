package com.example.blogwiser

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
        val v:ViewGroup
        val logout:MaterialButton
        val myblog:MaterialButton
        v=inflater.inflate(R.layout.fragment_setting, container, false) as ViewGroup
        logout=v.findViewById(R.id.logout)
        logout.setOnClickListener(View.OnClickListener {
            val intent: Intent = Intent(context,intro::class.java)
            startActivity(intent)
        })
        myblog=v.findViewById(R.id.myblogs)
        myblog.setOnClickListener(View.OnClickListener {
            val intent: Intent = Intent(context,myblogs::class.java)
            startActivity(intent)
            requireActivity().finish()
        })
        return v
    }
}