package com.pnu.aidbtdiary.network

import CompletionResponse
import com.pnu.aidbtdiary.dto.OpenAiRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiAPIService {
    @POST("chat/completions")
    suspend fun getResponse(
        @Body request: OpenAiRequest
    ): CompletionResponse
}