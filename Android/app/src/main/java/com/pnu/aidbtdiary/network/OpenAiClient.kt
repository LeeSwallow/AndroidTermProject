package com.pnu.aidbtdiary.network

import com.pnu.aidbtdiary.BuildConfig
import CompletionResponse
import com.pnu.aidbtdiary.dto.OpenAiRequest
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

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
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(OpenAiAPIService::class.java)
    }

    suspend fun getCompletion(request: OpenAiRequest): CompletionResponse {
        return api.getResponse(request)
    }

    suspend fun getSpeechStream(prompt: String): ResponseBody {
        val ttsRequest = com.pnu.aidbtdiary.dto.TTSRequest(
            input = prompt,
            instructions = "Generate a speech in a warm and supportive tone."
        )
        return api.getSpeech(ttsRequest)
    }
}

