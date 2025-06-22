package com.tu.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.tu.project.R
import com.tu.project.databinding.ActivityMypageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageBinding  // 뷰바인딩 객체 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)  // 바인딩 인플레이트
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.home4.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        binding.calendar4.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
            finish()
        }
        binding.feed4.setOnClickListener {
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }

        binding.btnLogout.setOnClickListener {
            // SharedPreferences 에 저장된 토큰 삭제
            val editor = getSharedPreferences("auth", MODE_PRIVATE).edit()
            editor.clear()  // 모든 저장 데이터 삭제 (필요하면 accessToken, refreshToken만 remove()로 개별 삭제 가능)
            editor.apply()

            // 로그인 화면으로 이동
            startActivity(Intent(this, LoginActivity::class.java))

            // 현재 액티비티 종료
            finish()
        }

        // SharedPreferences에서 토큰 읽기
        val accessToken = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("accessToken", null)

        if (accessToken != null) {
            RetrofitClient.instance.getUserInfo("Bearer $accessToken")
                .enqueue(object : Callback<UserInfoResponse> {
                    override fun onResponse(
                        call: Call<UserInfoResponse>,
                        response: Response<UserInfoResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            val user = response.body()!!.result
                            if (user != null) {
                                binding.tvNickname.text = user.username
                                binding.tvEmail.text = user.email
                                binding.tvRegionPoints.text = "지역 : ${user.region} | 시루 포인트 : 9,000,000,000"
                                Glide.with(this@MypageActivity)
                                    .load(user.profileImageUrl)
                                    //.placeholder(R.drawable.ic_user_placeholder)
                                    .circleCrop()
                                    .into(binding.ivProfile)
                            }
                        } else {
                            Toast.makeText(this@MypageActivity, "사용자 정보를 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                        Toast.makeText(this@MypageActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
