package com.pnu.aidbtdiary.dto

import com.google.gson.annotations.SerializedName

data class Candidate(
    val content: Content,
    val index: Int
)

data class UsageMetadata(
    @SerializedName("promptTokenCount") val promptTokenCount: Int,
    @SerializedName("candidatesTokenCount") val candidatesTokenCount: Int,
    @SerializedName("totalTokenCount") val totalTokenCount: Int,
    @SerializedName("thoughtsTokenCount") val thoughtsTokenCount: Int
)

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<Candidate>,
    @SerializedName("usageMetadata") val usageMetadata: UsageMetadata,
) {
    fun getResponseText(): String {
        return candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
    }
}