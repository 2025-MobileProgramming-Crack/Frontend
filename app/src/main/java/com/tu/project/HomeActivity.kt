package com.tu.project

import com.tu.project.ContestActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.CalendarView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tu.project.R
import com.tu.project.adapter.TaskAdapter
import com.tu.project.CalendarEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<CalendarEvent>()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = getSharedPreferences("auth", MODE_PRIVATE).getString("accessToken", null)
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

        // 오늘의 할 일 RecyclerView 세팅
        val rvTasks = findViewById<RecyclerView>(R.id.rvTasksHome)
        taskAdapter = TaskAdapter(taskList) { /* Home에서는 삭제 기능 없음 */ }
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter

        // CalendarView에서 날짜 선택 시 해당 날짜의 할 일 표시
        val calendarView = findViewById<CalendarView>(R.id.calendarViewHome)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            fetchEventsForDate(selectedDate)
        }

        // 앱 시작 시 오늘 날짜의 할 일 표시
        val today = sdf.format(Date())
        fetchEventsForDate(today)
    }

    private fun fetchEventsForDate(date: String) {
        val token = getSharedPreferences("auth", MODE_PRIVATE).getString("accessToken", null) ?: return
        RetrofitClient.instance.getEvents("Bearer $token", date).enqueue(object : Callback<CalendarResponse> {
            override fun onResponse(call: Call<CalendarResponse>, response: Response<CalendarResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    taskList.clear()
                    taskList.addAll(response.body()?.result ?: emptyList())
                    taskAdapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<CalendarResponse>, t: Throwable) {}
        })
    }
}