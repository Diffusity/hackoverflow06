package com.example.blogwiser

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import kotlin.math.sign

class intro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val logi:MaterialButton
        val signu:MaterialButton
        logi=findViewById(R.id.login)
        signu=findViewById(R.id.signup)
        logi.setOnClickListener(View.OnClickListener {
            val intent= Intent(this,login::class.java)
            startActivity(intent)
        })
        signu.setOnClickListener(View.OnClickListener {
            val intent= Intent(this,signup::class.java)
            startActivity(intent)
        })
    }
}