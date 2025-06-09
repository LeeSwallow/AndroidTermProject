package com.pnu.aidbtdiary.network

import com.pnu.aidbtdiary.BuildConfig
import CompletionResponse
import com.pnu.aidbtdiary.dto.CompletionRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenAiClient {
    companion object {
        private val okHttpAiClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.OPENAI_BASE_URL)
            .client(okHttpAiClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api: OpenAiAPIService = retrofit.create(OpenAiAPIService::class.java)

        suspend fun getCompletion(request: CompletionRequest): CompletionResponse {
            return api.getResponse(request)
        }
    }
}