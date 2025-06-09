package com.pnu.aidbtdiary.helper

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pnu.aidbtdiary.entity.DbtDiary
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.time.LocalDateTime

data class DbtDiaryListWrapper(
    val contents: List<DbtDiary>
)

object ConvertToJsonHelper {
    fun exportDbtDiaryListToJson(context: Context, diaryList: List<DbtDiary>): Uri? {
        val gson = Gson()
        val wrapper = DbtDiaryListWrapper(diaryList)
        val jsonString = gson.toJson(wrapper)

        val fileName = "export_" + LocalDateTime.now().toString() + ".json"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        FileOutputStream(file).use { it.write(jsonString.toByteArray()) }
        return Uri.fromFile(file)
    }

    fun downloadJsonFile(context: Context, fileUri: Uri) {
        val request = DownloadManager.Request(fileUri)
            .setTitle("DBT Diary Export")
            .setDescription("DBT Diary 데이터를 JSON으로 내보냅니다.")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileUri.lastPathSegment)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setMimeType("application/json")

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
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
                onSuccess(wrapper.contents)
            } ?: onError(Exception("파일을 열 수 없습니다."))
        } catch (e: Exception) {
            onError(e)
        }
    }
}