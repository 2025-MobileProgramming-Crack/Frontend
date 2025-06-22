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
import com.tu.project.databinding.ActivityAddscheduleBinding


class AddscheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddscheduleBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddscheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        binding.ivBackArrow.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
            finish()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val logonButton: Button = findViewById(R.id.btnAddEvent)
        logonButton.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }
    }
}