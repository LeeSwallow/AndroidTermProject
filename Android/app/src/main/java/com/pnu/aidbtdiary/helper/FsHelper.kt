package com.pnu.aidbtdiary.helper

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pnu.aidbtdiary.entity.DbtDiary
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FsHelper(private val context: Context) {

    private data class DbtDiaryWrapper(
        val date: String,
        val situation: String,
        val emotion: String,
        val intensity: Int,
        val thought: String,
        val behavior: String,
        val dbtSkill: String,
        val solution: String,
        val deleted: Boolean,
        val sentiment: Boolean,
        val createdAt: String,
        val updatedAt: String
    )


    private data class DbtDiaryListWrapper(
        val contents: List<DbtDiaryWrapper>
    )

    private fun fromDbtDiaryTOWrapper(diary: DbtDiary): DbtDiaryWrapper {
        return DbtDiaryWrapper(
            date = diary.date.toString(),
            situation = diary.situation,
            emotion = diary.emotion,
            intensity = diary.intensity,
            thought = diary.thought,
            behavior = diary.behavior,
            dbtSkill = diary.dbtSkill,
            solution = diary.solution,
            deleted = diary.deleted,
            sentiment = diary.sentiment,
            createdAt = diary.createdAt.toString(),
            updatedAt = diary.updatedAt.toString()
        )
    }
    private fun fromWrapperToDbtDiary(wrapper: DbtDiaryWrapper): DbtDiary {
        return DbtDiary(
            date = LocalDate.parse(wrapper.date, DateTimeFormatter.ISO_DATE),
            situation = wrapper.situation,
            emotion = wrapper.emotion,
            intensity = wrapper.intensity,
            thought = wrapper.thought,
            behavior = wrapper.behavior,
            dbtSkill = wrapper.dbtSkill,
            solution = wrapper.solution,
            deleted = wrapper.deleted,
            sentiment = wrapper.sentiment,
            createdAt = LocalDateTime.parse(wrapper.createdAt, DateTimeFormatter.ISO_DATE_TIME),
            updatedAt = LocalDateTime.parse(wrapper.updatedAt, DateTimeFormatter.ISO_DATE_TIME)
        )
    }

    fun downloadDbtDiaryToJson(diaries: List<DbtDiary>, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        val gson = Gson()
        val wrapper = DbtDiaryListWrapper(
            contents = diaries.map { fromDbtDiaryTOWrapper(it) }
        )
        val jsonString = gson.toJson(wrapper)
        val fileName = "export_" + LocalDateTime.now().toString().replace(":", "-") + ".json"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/json")
                }
                val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val itemUri = resolver.insert(collection, contentValues)
                if (itemUri != null) {
                    resolver.openOutputStream(itemUri)?.use { outputStream ->
                        outputStream.write(jsonString.toByteArray())
                        outputStream.flush()
                    }
                    onResult(true, "다운로드가 완료되었습니다: $fileName")
                } else {
                    onResult(false, "파일을 저장할 수 없습니다.")
                }
            } else {
                // Android 12 이하: 기존 방식
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                FileOutputStream(file).use { it.write(jsonString.toByteArray()) }
                onResult(true, "다운로드가 완료되었습니다: $fileName")
            }
        } catch (e: Exception) {
            onResult(false, "저장 중 오류 발생: ${e.localizedMessage}")
        }
    }

    fun importDbtDiaryListFromJson(
        context: Context,
        fileUri: Uri,
        onSuccess: (List<DbtDiary>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                val reader = InputStreamReader(inputStream)
                val type = object : TypeToken<DbtDiaryListWrapper>() {}.type
                val wrapper = Gson().fromJson<DbtDiaryListWrapper>(reader, type)
                onSuccess(wrapper.contents.map { fromWrapperToDbtDiary(it) })
            } ?: onError(Exception("파일을 열 수 없습니다."))
        } catch (e: Exception) {
            onError(e)
        }
    }

    suspend fun syncToLocal(diaries: List<DbtDiary>, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        try {
            val db = AppDatabaseHelper.getDatabase(context)
            val dao = db.dbtDiaryDao()
            dao.insertAll(diaries)
            onSuccess()

        } catch (e: Exception) {
            onError(e)
        }
    }
}

