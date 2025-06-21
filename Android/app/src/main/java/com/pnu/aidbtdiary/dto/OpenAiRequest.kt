package com.pnu.aidbtdiary.dto

data class OpenAiRequest(
    val model: String,
    val messages: List<Message>
)

data class Message (
    val role: String,
    val content: String
)