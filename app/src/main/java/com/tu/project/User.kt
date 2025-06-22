package com.tu.project

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val region: String
)

data class SignUpResponse(
    val message: String,
    val result: String?,
    val errorCode: String?,
    val success: Boolean
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class JwtToken(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String
)

data class LoginResult(
    val jwtToken: JwtToken
)

data class LoginResponse(
    val message: String,
    val result: LoginResult?,
    val errorCode: String?,
    val success: Boolean
)

data class UserInfoResponse(
    val message: String,
    val result: UserInfo?,
    val errorCode: String,
    val success: Boolean
)

data class UserInfo(
    val email: String,
    val username: String,
    val phoneNumber: String,
    val region: String,
    val profileImageUrl: String
)