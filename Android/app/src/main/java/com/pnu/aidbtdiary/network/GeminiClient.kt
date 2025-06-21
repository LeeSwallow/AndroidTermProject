package com.pnu.aidbtdiary.network

import com.pnu.aidbtdiary.BuildConfig
import com.pnu.aidbtdiary.dto.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class GeminiClient {
    val api : GeminiAPIService
    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.GEMINI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(GeminiAPIService::class.java)
    }

    suspend fun getCompletion(request: GeminiRequest): GeminiResponse {
        return api.getCompletion(
            model=BuildConfig.GEMINI_MODEL,
            apiKey=BuildConfig.GEMINI_API_KEY,
            body=request)
    }
}
