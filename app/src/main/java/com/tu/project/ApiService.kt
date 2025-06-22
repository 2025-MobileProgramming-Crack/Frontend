package com.tu.project

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/user/signUp")
    fun signUp(@Body body: SignUpRequest): Call<SignUpResponse>

    @POST("/user/logIn")
    fun login(@Body body: LoginRequest): Call<LoginResponse>


}
