package com.pnu.aidbtdiary.dto

data class TTSRequest(
    val model: String = "gpt-4o-mini-tts",
    val voice: String = "coral",
    val response_format: String = "wav",
    val input: String,
    val instructions: String
)
