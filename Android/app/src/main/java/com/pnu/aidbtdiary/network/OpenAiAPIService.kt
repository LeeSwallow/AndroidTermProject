package com.pnu.aidbtdiary.network

import CompletionResponse
import com.pnu.aidbtdiary.dto.OpenAiRequest
import com.pnu.aidbtdiary.dto.TTSRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OpenAiAPIService {
    @POST("chat/completions")
    suspend fun getResponse(
        @Body request: OpenAiRequest
    ): CompletionResponse

    @Streaming
    @POST("audio/speech")
    suspend fun getSpeech(
        @Body request: TTSRequest
    ): ResponseBody
}

