package com.pnu.aidbtdiary.network

import com.pnu.aidbtdiary.BuildConfig
import com.pnu.aidbtdiary.dto.DbtDiaryRemote
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

class SupaClient{
    private val client: SupabaseClient =
        createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_KEY,) { install(Postgrest) }

    private val dbtDiaryTable = client.postgrest.from("dbt_diary")

    suspend fun getAllDiaries(): List<DbtDiaryRemote> {
        return dbtDiaryTable.select().decodeList<DbtDiaryRemote>()
    }

    suspend fun insertDiary(diary: DbtDiaryRemote): DbtDiaryRemote? {
        return dbtDiaryTable.insert(diary).decodeSingle()
    }

    suspend fun insertDiaries(diaries: List<DbtDiaryRemote>): List<DbtDiaryRemote> {
        return dbtDiaryTable.insert(diaries).decodeList()
    }

    suspend fun updateDiary(diary: DbtDiaryRemote): DbtDiaryRemote? {
        return dbtDiaryTable.update(diary).decodeSingle()
    }

    suspend fun updateDiaries(diaries: List<DbtDiaryRemote>): List<DbtDiaryRemote> {
        return dbtDiaryTable.update(diaries).decodeList()
    }
}