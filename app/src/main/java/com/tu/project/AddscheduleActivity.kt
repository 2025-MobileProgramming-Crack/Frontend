package com.tu.project

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tu.project.databinding.ActivityAddscheduleBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AddscheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddscheduleBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddscheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ⬅️ 뒤로가기 버튼
        binding.ivBackArrow.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
            finish()
        }

        // 📅 날짜 입력 클릭 시 DatePickerDialog 표시
        binding.etEventDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    binding.etEventDate.setText(dateStr)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // ➕ 일정 추가 버튼 클릭 시 서버 요청
        binding.btnAddEvent.setOnClickListener {
            val title = binding.etEventTitle.text.toString().trim()
            val description = binding.etEventContent.text.toString().trim()
            val dateStr = binding.etEventDate.text.toString().trim()

            if (title.isEmpty() || dateStr.isEmpty()) {
                Toast.makeText(this, "제목과 날짜를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fullDateTime = try {
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val parsed = formatter.parse(dateStr)
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(parsed!!)
            } catch (e: Exception) {
                Toast.makeText(this, "날짜 형식을 확인하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = getSharedPreferences("auth", MODE_PRIVATE).getString("accessToken", null)
                ?: return@setOnClickListener
            val isAdmin = getSharedPreferences("auth", MODE_PRIVATE).getBoolean("isAdmin", false)

            val request = AddEventRequest(
                title = title,
                description = description,
                date = fullDateTime
            )

            RetrofitClient.instance.addEvent("Bearer $token", request)
                .enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                        Log.d("Addschedule", "HTTP code: ${response.code()}")
                        if (!response.isSuccessful) {
                            Log.e("Addschedule", "Error body: ${response.errorBody()?.string()}")
                        }
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(
                                this@AddscheduleActivity,
                                if (isAdmin) "전체 사용자에게 일정이 추가되었습니다." else "일정이 추가되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@AddscheduleActivity, CalendarActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@AddscheduleActivity, "일정 추가 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                        Log.e("Addschedule", "네트워크 오류", t)
                        Toast.makeText(this@AddscheduleActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}