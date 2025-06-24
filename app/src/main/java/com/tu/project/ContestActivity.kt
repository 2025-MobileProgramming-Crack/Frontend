package com.tu.project
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.tu.project.HomeActivity
import com.tu.project.LikeTopPostResponse
import com.tu.project.ParticipateActivity
import com.tu.project.RetrofitClient
import com.tu.project.databinding.ActivityContestBinding
import com.tu.project.databinding.ItemTopPostBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack2.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.btnParticipate.setOnClickListener {
            val intent = Intent(this, ParticipateActivity::class.java)
            startActivity(intent)
        }

        // ✅ 좋아요 TOP3 게시글 불러오기
        loadTop3Posts()
    }

    private fun loadTop3Posts() {
        RetrofitClient.instance.getTopLikedPosts().enqueue(object : Callback<LikeTopPostResponse> {
            override fun onResponse(call: Call<LikeTopPostResponse>, response: Response<LikeTopPostResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val posts = response.body()?.result?.take(3) ?: return
                    binding.llTop3List.removeAllViews() // 기존 뷰 제거

                    for (post in posts) {
                        val itemBinding = ItemTopPostBinding.inflate(layoutInflater, binding.llTop3List, false)

                        itemBinding.tvTopTitle.text = post.title
                        itemBinding.tvTopUser.text = "@${post.userName} | ${post.likeCount} likes"
                        Glide.with(this@ContestActivity)
                            .load(post.imageUrl)
                            .into(itemBinding.ivTopImage)

                        // 동적으로 이미지가 있으면 배경을 투명하게
                        if (!post.imageUrl.isNullOrEmpty()) {
                            itemBinding.flTopImageContainer.setBackgroundResource(android.R.color.transparent)
                        }

                        binding.llTop3List.addView(itemBinding.root)
                    }
                } else {
                    Toast.makeText(this@ContestActivity, "Top 3 게시글을 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LikeTopPostResponse>, t: Throwable) {
                Toast.makeText(this@ContestActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
