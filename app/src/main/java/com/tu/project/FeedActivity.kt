package com.tu.project

import FeedAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.tu.project.R
import com.tu.project.databinding.ActivityFeedBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.mypage4.setOnClickListener {
            startActivity(Intent(this, MypageActivity::class.java))
            finish()
        }

        binding.recyclerFeed.layoutManager = LinearLayoutManager(this)

        RetrofitClient.instance.getAllPosts().enqueue(object : Callback<FeedPostResponse> {
            override fun onResponse(call: Call<FeedPostResponse>, response: Response<FeedPostResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val posts = response.body()?.result ?: emptyList()
                    binding.recyclerFeed.adapter = FeedAdapter(posts)
                } else {
                    Toast.makeText(this@FeedActivity, "게시글 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FeedPostResponse>, t: Throwable) {
                Toast.makeText(this@FeedActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}