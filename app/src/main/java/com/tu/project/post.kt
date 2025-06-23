package com.tu.project

data class UploadResponse(
    val message: String,
    val result: String?,
    val errorCode: String?,
    val success: Boolean
)

data class MyPostResponse(
    val message: String,
    val result: List<MyPost>,
    val errorCode: String?,
    val success: Boolean
)

data class MyPost(
    val id: Int,
    val userName: String,
    val title: String,
    val imageUrl: String,
    val likeCount: Int,
    val updatedAt: String
)