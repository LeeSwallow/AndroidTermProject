package com.pnu.aidbtdiary.helper

import android.content.Context
import com.pnu.aidbtdiary.dto.DbtDiaryRemote
import com.pnu.aidbtdiary.entity.DbtDiary
import com.pnu.aidbtdiary.network.SupaClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.time.LocalDate
import java.time.LocalDateTime

class SyncHelper(private val context: Context) {
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

    suspend fun syncDiaries() {
        val localDiaries = dao.getAll()
        val remoteDiaries = supaClient.getAllDiaries()
        val remoteDiariesConverted = remoteToLocal(remoteDiaries)
        val (localResult, remoteResult) = findUpdatedDiaries(localDiaries, remoteDiariesConverted)
        try {
            if (localResult.needCreate .isNotEmpty() || localResult.needUpdate.isNotEmpty()) {
                val sumResult = localResult.needCreate + localResult.needUpdate
                dao.insertAll(sumResult)
            }
            if (remoteResult.needUpdate.isNotEmpty()) {
                supaClient.upsertDiaries(localToRemote(remoteResult.needUpdate))
            }
            if (remoteResult.needCreate.isNotEmpty()) {
                supaClient.insertDiaries(localToRemote(remoteResult.needCreate))
            }

            withContext(Dispatchers.Main) {
                NotificationUtil.notifySyncComplete(context)
            }
        } catch (e: Throwable) {
            withContext(Dispatchers.Main) {
                NotificationUtil.notifySyncError(context, e.message ?: "알 수 없는 오류")
            }
            return
        }
    }

    private fun findUpdatedDiaries(diaries1: List<DbtDiary>, diaries2: List<DbtDiary>):
            Pair<SyncResult, SyncResult> {
        val resultCreate1 = mutableListOf<DbtDiary>()
        val resultUpdate1 = mutableListOf<DbtDiary>()
        val resultCreate2 = mutableListOf<DbtDiary>()
        val resultUpdate2 = mutableListOf<DbtDiary>()

        val searchMap = diaries1.associateBy { it.id }.toMutableMap()
        for (diary in diaries2) {
            val baseDiary = searchMap[diary.id]
            if (baseDiary == null) {
                resultCreate1.add(diary)
            } else {
                if (baseDiary.updatedAt.isBefore(diary.updatedAt)) {
                    resultUpdate1.add(diary)
                } else if (baseDiary.updatedAt.isAfter(diary.updatedAt)) {
                    resultUpdate2.add(baseDiary)
                }
                searchMap.remove(diary.id)
            }
        }

        resultCreate2.addAll(searchMap.values)
        return SyncResult(resultUpdate1, resultCreate1) to SyncResult(resultUpdate2, resultCreate2)
    }

    data class SyncResult(
        val needUpdate: List<DbtDiary>,
        val needCreate: List<DbtDiary>
    )
}
