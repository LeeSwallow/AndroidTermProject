package com.pnu.aidbtdiary.helper

import com.pnu.aidbtdiary.entity.DbtDiary

object SyncHelper {
    
    fun getUpdatedOrNewDiaries(base: List<DbtDiary>, input: List<DbtDiary>): List<DbtDiary> {
        val baseMap = base.associateBy { it.id }
        return input.filter { diary ->
            val existing = baseMap[diary.id]
            existing == null || diary.updatedAt.isAfter(existing.updatedAt)
        }
    }
}