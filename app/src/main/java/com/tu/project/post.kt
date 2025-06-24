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

data class FeedPostResponse(
    val message: String,
    val result: List<FeedPost>,
    val errorCode: String?,
    val success: Boolean
)

data class FeedPost(
    val id: Int,
    val userName: String,
    val title: String,
    val imageUrl: String,
    val likeCount: Int,
    val updatedAt: String
)

data class LikeTopPostResponse(
    val message: String,
    val result: List<TopPost>,
    val errorCode: String?,
    val success: Boolean
)

data class TopPost(
    val id: Int,
    val userName: String,
    val title: String,
    val imageUrl: String,
    val likeCount: Int,
    val updatedAt: String
)
