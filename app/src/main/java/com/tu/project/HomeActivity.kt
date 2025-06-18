package com.tu.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tu.project.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<ImageView>(R.id.calendar1).setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.feed1).setOnClickListener {
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.mypage1).setOnClickListener {
            startActivity(Intent(this, MypageActivity::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.contest1).setOnClickListener {
            startActivity(Intent(this, ContestActivity::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


    }



}