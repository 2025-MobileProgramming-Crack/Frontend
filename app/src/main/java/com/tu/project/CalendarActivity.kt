package com.tu.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.tu.project.adapter.TaskAdapter
import com.tu.project.databinding.ActivityCalendarBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarBinding
    private val taskList = mutableListOf<CalendarEvent>()
    private lateinit var taskAdapter: TaskAdapter
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupNavigation()
        setupRecyclerView()
        setupCalendar()
        fetchMonthEvents()
    }

    private fun setupNavigation() {
        binding.home2.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        binding.feed2.setOnClickListener {
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }
        binding.mypage2.setOnClickListener {
            startActivity(Intent(this, MypageActivity::class.java))
            finish()
        }
        binding.btnAddEvent.setOnClickListener {
            startActivity(Intent(this, AddscheduleActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(taskList) { event ->
            deleteEvent(event)
        }
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = taskAdapter
    }

    private fun setupCalendar() {
        val today = sdf.format(Date())
        fetchEventsForDate(today)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            fetchEventsForDate(selectedDate)
        }
    }

    private fun fetchEventsForDate(date: String) {
        val token = getSharedPreferences("auth", MODE_PRIVATE).getString("accessToken", null) ?: return
        RetrofitClient.instance.getEvents("Bearer $token", date).enqueue(object : Callback<CalendarResponse> {
            override fun onResponse(call: Call<CalendarResponse>, response: Response<CalendarResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    taskList.clear()
                    taskList.addAll(response.body()?.result ?: emptyList())
                    taskAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@CalendarActivity, "일정 조회 실패", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<CalendarResponse>, t: Throwable) {
                Toast.makeText(this@CalendarActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchMonthEvents() {
        val token = getSharedPreferences("auth", MODE_PRIVATE).getString("accessToken", null) ?: return
        RetrofitClient.instance.getMonthEvents("Bearer $token").enqueue(object : Callback<MonthResponse> {
            override fun onResponse(call: Call<MonthResponse>, response: Response<MonthResponse>) {
                // 날짜 강조 UI 등을 여기에 구현 가능
            }
            override fun onFailure(call: Call<MonthResponse>, t: Throwable) {}
        })
    }

    private fun deleteEvent(event: CalendarEvent) {
        val token = getSharedPreferences("auth", MODE_PRIVATE).getString("accessToken", null) ?: return
        Log.d("DeleteEvent", "token: $token, id: ${event.id}")

        RetrofitClient.instance.deleteEvent("Bearer $token", event.id).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                val errorBody = response.errorBody()?.string()
                Log.d("DeleteEvent", "response code: ${response.code()}, errorBody: $errorBody")

                if (response.isSuccessful && response.body()?.success == true) {
                    taskList.remove(event)
                    taskAdapter.notifyDataSetChanged()
                    Toast.makeText(this@CalendarActivity, "삭제됨", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CalendarActivity, "삭제 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Log.e("DeleteEvent", "onFailure: ${t.message}", t)
                Toast.makeText(this@CalendarActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
