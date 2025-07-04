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
    var solution: String = ""
    var sentiment: Boolean = false

    constructor(date: LocalDate) {
        this.date = date
    }

    fun isValid(): Boolean {
        return situation.isNotEmpty() && emotion.isNotEmpty() && intensity >= 0
    }

    fun getDate(): LocalDate {
        return date
    }

    fun toEntity(): DbtDiary {
        return DbtDiary(
            date = date,
            situation = situation,
            emotion = emotion,
            intensity = intensity,
            thought = thought,
            behavior = behavior,
            dbtSkill = dbtSkill,
            solution = solution,
            deleted = false,
            sentiment = sentiment,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }
}