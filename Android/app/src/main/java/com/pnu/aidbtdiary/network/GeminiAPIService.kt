package com.pnu.aidbtdiary.network

import com.pnu.aidbtdiary.dto.GeminiRequest
import com.pnu.aidbtdiary.dto.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GeminiAPIService {
    @POST("models/{model}:generateContent")
    suspend fun getCompletion(
        @Path("model", encoded = true) model: String,
        @Query("key") apiKey: String,
        @Body body: GeminiRequest
    ): GeminiResponse
}

