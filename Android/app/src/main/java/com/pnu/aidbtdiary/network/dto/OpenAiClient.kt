package com.pnu.aidbtdiary.network.dto

import CompletionResponse
import com.pnu.aidbtdiary.BuildConfig
import com.pnu.aidbtdiary.BuildConfig.OPENAI_BASE_URL
import com.pnu.aidbtdiary.network.OpenAiAPIService
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
            .baseUrl(OPENAI_BASE_URL)
            .client(okHttpAiClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api: OpenAiAPIService = retrofit.create(OpenAiAPIService::class.java)

        suspend fun getCompletion(request: CompletionRequest): CompletionResponse {
            return api.getResponse(request)
        }
    }
}