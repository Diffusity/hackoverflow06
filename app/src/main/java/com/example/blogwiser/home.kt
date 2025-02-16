package com.example.blogwiser

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import com.example.blogwiser.adapters.viewPageradapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val view: ViewPager
        val tab: TabLayout
        val adapter: viewPageradapter
        tab=findViewById(R.id.tab)
        view=findViewById(R.id.viewPager)
        adapter=viewPageradapter(supportFragmentManager)
        view.adapter=adapter
        tab.setupWithViewPager(view)
        tab.setSelectedTabIndicatorColor(Color.parseColor("#fff387"))
        tab.setTabTextColors(ColorStateList.valueOf(Color.parseColor("#D6DCED")))

    }
}