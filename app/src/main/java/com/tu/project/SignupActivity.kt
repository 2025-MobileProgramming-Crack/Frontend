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
import com.tu.project.R
import com.tu.project.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
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

        binding.btnSignUp2.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val passwordConfirm = binding.etPasswordConfirm.text.toString()
            val username = binding.etNickname.text.toString()
            val region = binding.etRegion.text.toString()
            val phoneNumber = binding.etPhone.text.toString()

            if (password != passwordConfirm) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = SignUpRequest(username, email, password, phoneNumber, region)

            RetrofitClient.instance.signUp(request).enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    Log.d("회원가입", "HTTP 코드: ${response.code()}")

                    if (response.isSuccessful) {

                        Toast.makeText(this@SignupActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    } else {
                        Toast.makeText(this@SignupActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    Toast.makeText(this@SignupActivity, "에러: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

