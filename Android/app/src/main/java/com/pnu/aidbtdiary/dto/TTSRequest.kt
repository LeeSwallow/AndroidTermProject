package com.pnu.aidbtdiary.dto

import com.google.gson.annotations.SerializedName

data class TTSRequest(
    val model: String = "gpt-4o-mini-tts",
    val voice: String = "coral",
    @SerializedName("response_format") val responseFormat: String = "pcm",
    val input: String,
    val instructions: String
)
