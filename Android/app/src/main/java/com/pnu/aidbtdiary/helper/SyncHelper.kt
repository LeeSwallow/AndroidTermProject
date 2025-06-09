package com.pnu.aidbtdiary.helper

import com.pnu.aidbtdiary.dto.DbtDiaryRemote
import com.pnu.aidbtdiary.entity.DbtDiary
import java.time.LocalDate
import java.time.LocalDateTime

object SyncHelper {


    fun remoteToLocal(from: List<DbtDiaryRemote>): List<DbtDiary> {
        return from.map {
            DbtDiary(
                id = it.id,
                date = LocalDate.parse(it.date),
                situation = it.situation,
                emotion = it.emotion,
                intensity = it.intensity,
                thought = it.thought,
                behavior = it.behavior,
                dbtSkill = it.dbt_skill,
                deleted = it.deleted,
                sentiment = it.sentiment,
                createdAt = LocalDateTime.parse(it.created_at),
                updatedAt = LocalDateTime.parse(it.updated_at)
            )
        }
    }

    fun localToRemote(from: List<DbtDiary>): List<DbtDiaryRemote> {
        return from.map {
            DbtDiaryRemote(
                id = it.id,
                date = it.date.toString(),
                situation = it.situation,
                emotion = it.emotion,
                intensity = it.intensity,
                thought = it.thought,
                behavior = it.behavior,
                dbt_skill = it.dbtSkill,
                deleted = it.deleted,
                sentiment = it.sentiment,
                created_at = it.createdAt.toString(),
                updated_at = it.updatedAt.toString()
            )
        }
    }
}