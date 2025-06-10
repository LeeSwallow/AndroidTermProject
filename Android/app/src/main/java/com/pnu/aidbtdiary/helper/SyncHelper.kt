package com.pnu.aidbtdiary.helper

import android.content.Context
import com.pnu.aidbtdiary.dto.DbtDiaryRemote
import com.pnu.aidbtdiary.entity.DbtDiary
import com.pnu.aidbtdiary.network.SupaClient
import java.security.SecureRandom
import java.time.LocalDate
import java.time.LocalDateTime

class SyncHelper(context: Context) {
    private val dao = AppDatabaseHelper.getDatabase(context).dbtDiaryDao()
    private val supaClient = SupaClient()

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
                solution = it.solution,
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
                solution = it.solution,
                deleted = it.deleted,
                sentiment = it.sentiment,
                created_at = it.createdAt.toString(),
                updated_at = it.updatedAt.toString()
            )
        }
    }

    suspend fun syncDiaries(onSuccess : () -> Unit, onError: (Throwable) -> Unit) {
        val localDiaries = dao.getAll()
        val remoteDiaries = supaClient.getAllDiaries()
        val remoteDiariesConverted = remoteToLocal(remoteDiaries)
        val (localResult, remoteResult) = findUpdatedDiaries(localDiaries, remoteDiariesConverted)
        try {
            if (localResult.needCreate.isNotEmpty()) dao.insertAll(localResult.needCreate)
            if (localResult.needUpdate.isNotEmpty()) dao.updateAll(localResult.needUpdate)

            if (remoteResult.needCreate.isNotEmpty())
                supaClient.insertDiaries(localToRemote(remoteResult.needCreate))
            if (remoteResult.needUpdate.isNotEmpty())
                supaClient.updateDiaries(localToRemote(remoteResult.needUpdate))
        } catch (e: Throwable) {
            onError(e)
        }
        onSuccess()
    }

    private fun findUpdatedDiaries(diaries1: List<DbtDiary>, diaries2: List<DbtDiary>):
            Pair<SearchedResult, SearchedResult> {
        val createdDiaries1 = mutableListOf<DbtDiary>()
        val updatedDiaries1 = mutableListOf<DbtDiary>()
        val createdDiaries2 = mutableListOf<DbtDiary>()
        val updatedDiaries2 = mutableListOf<DbtDiary>()
        val searchMap = diaries1.associateBy { it.id }.toMutableMap()

        for (diary in diaries2) {
            val baseDiary = searchMap[diary.id]
            if (baseDiary == null) {
                createdDiaries1.add(diary)
            } else {
                if (baseDiary.updatedAt.isBefore(diary.updatedAt)) {
                    updatedDiaries1.add(diary)
                } else if (baseDiary.updatedAt.isAfter(diary.updatedAt)) {
                    updatedDiaries2.add(baseDiary)
                }
                searchMap.remove(diary.id)
            }
        }
        createdDiaries2.addAll(searchMap.values)

        return Pair(SearchedResult(updatedDiaries1, createdDiaries1), SearchedResult(updatedDiaries2, createdDiaries2))
    }
}

data class SearchedResult (
    val needUpdate : List<DbtDiary>,
    val needCreate : List<DbtDiary>
)
