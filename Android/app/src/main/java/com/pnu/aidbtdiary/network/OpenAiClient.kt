package com.pnu.aidbtdiary.network

import com.pnu.aidbtdiary.BuildConfig
import CompletionResponse
import com.pnu.aidbtdiary.dto.CompletionRequest
import com.pnu.aidbtdiary.dto.TTSRequest
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenAiClient {
    val api:OpenAiAPIService
    init {
        val okHttpAiClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.OPENAI_BASE_URL)
            .client(okHttpAiClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(OpenAiAPIService::class.java)
    }

    suspend fun getCompletion(request: CompletionRequest): CompletionResponse {
        return api.getResponse(request)
    }

    suspend fun getSpeech(request: TTSRequest): ResponseBody {
        return api.getSpeech(request)
    }
}