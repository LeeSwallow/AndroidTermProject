package com.pnu.aidbtdiary.network

import CompletionResponse
import com.pnu.aidbtdiary.dto.CompletionRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiAPIService {
    @POST("responses")
    suspend fun getResponse(
        @Body request: CompletionRequest
    ): CompletionResponse
}