package com.pnu.aidbtdiary.dto

data class TTSRequest(
    val input: String,
    val instructions: String,
    val model: String,
    val voice: String
)