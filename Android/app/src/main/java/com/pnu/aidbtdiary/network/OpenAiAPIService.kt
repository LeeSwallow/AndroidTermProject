package com.pnu.aidbtdiary.network

import CompletionResponse
import com.pnu.aidbtdiary.dto.CompletionRequest
import com.pnu.aidbtdiary.dto.TTSRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiAPIService {
    @POST("chat/completions")
    suspend fun getResponse(
        @Body request: CompletionRequest
    ): CompletionResponse

    @POST("audio/speech")
    suspend fun getSpeech(
        @Body request: TTSRequest
    ): ResponseBody
}