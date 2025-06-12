package com.pnu.aidbtdiary.dto

import kotlinx.serialization.Serializable;

@Serializable
data class DbtDiaryRemote(
    val id: Long? = null,
    val date: String,
    val situation: String,
    val emotion: String,
    val intensity: Int,
    val thought: String,
    val behavior: String,
    val dbt_skill: String,
    val solution: String,
    val deleted: Boolean = false,
    val sentiment: Boolean,
    val created_at: String,
    val updated_at: String
) {
    fun removeId(): RemoteUpdate {
        return RemoteUpdate(
            date = date,
            situation = situation,
            emotion = emotion,
            intensity = intensity,
            thought = thought,
            behavior = behavior,
            dbt_skill = dbt_skill,
            solution = solution,
            deleted = deleted,
            sentiment = sentiment,
            created_at = created_at,
            updated_at = updated_at
        )
    }
}

@Serializable
data class RemoteUpdate (
    val date: String,
    val situation: String,
    val emotion: String,
    val intensity: Int,
    val thought: String,
    val behavior: String,
    val dbt_skill: String,
    val solution: String,
    val deleted: Boolean = false,
    val sentiment: Boolean,
    val created_at: String,
    val updated_at: String
)