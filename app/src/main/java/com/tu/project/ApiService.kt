package com.tu.project

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/user/signUp")
    fun signUp(@Body body: SignUpRequest): Call<SignUpResponse>

    @POST("/user/logIn")
    fun login(@Body body: LoginRequest): Call<LoginResponse>

    @GET("/user/getInfo")
    fun getUserInfo(
        @Header("Authorization") accessToken: String
    ): Call<UserInfoResponse>

    @GET("calendar")
    fun getEvents(@Header("Authorization") token: String, @Query("day") date: String): Call<CalendarResponse>

    @GET("calendar/month")
    fun getMonthEvents(@Header("Authorization") token: String): Call<MonthResponse>

    @DELETE("calendar/{date-id}")
    fun deleteEvent(@Header("Authorization") token: String, @Path("date-id") dateId: Long): Call<BasicResponse>

    @POST("calendar/create")
    fun addEvent(
        @Header("Authorization") token: String,
        @Body request: AddEventRequest
    ): Call<BasicResponse>
}
