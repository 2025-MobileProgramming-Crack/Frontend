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
import com.tu.project.R
import com.tu.project.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnLogin2.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val request = LoginRequest(email, password)

            RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val token = response.body()?.result?.jwtToken?.accessToken
                        Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    } else {
                        Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "에러: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}