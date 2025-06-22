package com.tu.project

data class AddEventRequest(
    val title: String,
    val description: String,
    val date: String  // ISO-8601 형식 (ex: "2025-06-22T15:00:00Z")
)

data class BasicResponse(
    val message: String,
    val result: Any?,  // Int or String 등 상황에 따라 다름
    val errorCode: String?,
    val success: Boolean
)

data class MonthResponse(
    val message: String,
    val result: List<String>,  // ["2025-06-22", ...]
    val errorCode: String?,
    val success: Boolean
)

data class CalendarResponse(
    val message: String,
    val result: List<CalendarEvent>,
    val errorCode: String?,
    val success: Boolean
)

data class CalendarEvent(
    val title: String,
    val description: String,
    val date: String  // ISO 형식 "2025-06-22T22:13:04.874Z"
)