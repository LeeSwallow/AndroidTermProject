package com.pnu.aidbtdiary.dto

import kotlinx.serialization.Serializable;

@Serializable
data class DbtDiaryRemote(
    val id: Long = 0,
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