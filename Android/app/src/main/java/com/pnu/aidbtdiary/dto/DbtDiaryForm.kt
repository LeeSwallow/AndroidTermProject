package com.pnu.aidbtdiary.dto

import com.pnu.aidbtdiary.entity.DbtDiary
import java.time.LocalDate
import java.time.LocalDateTime


class DbtDiaryForm {
    private val date: LocalDate
    var situation: String = ""
    var emotion: String = ""
    var intensity: Int = -1
    var thought: String = ""
    var behavior: String = ""
    var dbtSkill: String = ""

    constructor(date: LocalDate) {
        this.date = date
    }

    fun isValid(): Boolean {
        return situation.isNotEmpty() && emotion.isNotEmpty() && intensity >= 0;
    }


    fun toTemplate(): String? {
        if (!isValid()) return null
        return """
            날짜 $date
            상황: $situation
            감정: $emotion
            강도: $intensity
            생각: $thought
            행동: $behavior
            """
    }
    fun getDate(): LocalDate {
        return date
    }

    fun toEntityWithSentiment(sentiment: Boolean): DbtDiary {
        return DbtDiary(
            date = date,
            situation = situation,
            emotion = emotion,
            intensity = intensity,
            thought = thought,
            behavior = behavior,
            dbtSkill = dbtSkill,
            sentiment = sentiment,
            isSynced = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }
}