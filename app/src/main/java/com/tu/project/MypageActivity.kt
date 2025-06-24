package com.tu.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.tu.project.R
import com.tu.project.databinding.ActivityMypageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.activity.result.contract.ActivityResultContracts

class MypageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageBinding  // 뷰바인딩 객체 선언

    // 갤러리에서 이미지 선택 런처
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(binding.ivProfile)
            // 갤러리 아이콘 숨기기
            binding.ivGalleryIcon.visibility = android.view.View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 하단 네비게이션 바 버튼
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

        // 로그아웃 버튼
        binding.btnLogout.setOnClickListener {
            val editor = getSharedPreferences("auth", MODE_PRIVATE).edit()
            editor.clear()
            editor.apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // 프로필 이미지 클릭 시 갤러리 오픈
        binding.ivProfile.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // 로그인 토큰 확인 후 사용자 정보 + 게시글 요청
        val accessToken = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("accessToken", null)

        if (accessToken != null) {

            // ✅ 사용자 정보 요청
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
                                binding.tvRegionPoints.text = "지역 : ${user.region}"
                                Glide.with(this@MypageActivity)
                                    .load(user.profileImageUrl)
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

            // ✅ 내가 올린 게시글 목록 요청
            RetrofitClient.instance.getMyPosts("Bearer $accessToken")
                .enqueue(object : Callback<MyPostResponse> {
                    override fun onResponse(
                        call: Call<MyPostResponse>,
                        response: Response<MyPostResponse>
                    ) {
                        Log.e("MyPage", "응답 코드: ${response.code()}")
                        Log.e("MyPage", "응답 body: ${response.body()}")
                        Log.e("MyPage", "전체 응답: ${response.errorBody()?.string()}")
                        if (response.isSuccessful && response.body()?.success == true) {
                            val posts = response.body()?.result ?: emptyList()
                            Log.d("MyPage", "posts: $posts") // null 또는 size 0?
                            binding.rvMyPosts.adapter = MyPostAdapter(posts)
                            binding.rvMyPosts.layoutManager = LinearLayoutManager(this@MypageActivity)
                        } else {
                            Toast.makeText(this@MypageActivity, "게시글을 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<MyPostResponse>, t: Throwable) {
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
