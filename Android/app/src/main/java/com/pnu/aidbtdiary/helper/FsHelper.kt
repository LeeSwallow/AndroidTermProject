package com.pnu.aidbtdiary.helper

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pnu.aidbtdiary.entity.DbtDiary
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.time.LocalDateTime

class FsHelper(private val context: Context) {

    private data class DbtDiaryListWrapper(
        val contents: List<DbtDiary>
    )

    private fun exportDbtDiaryListToJson(diaryList: List<DbtDiary>): Uri? {
        val gson = Gson()
        val wrapper = DbtDiaryListWrapper(diaryList)
        val jsonString = gson.toJson(wrapper)

        val fileName = "export_" + LocalDateTime.now().toString() + ".json"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        FileOutputStream(file).use { it.write(jsonString.toByteArray()) }
        return Uri.fromFile(file)
    }

    fun saveSpeechToMp3(response : ResponseBody, fileName: String): File {
        val file = File(context.filesDir, fileName)
        response.byteStream().use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun downloadDbtDiaryToJson(diaries: List<DbtDiary>, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        val gson = Gson()
        val wrapper = DbtDiaryListWrapper(diaries)
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
                onSuccess(wrapper.contents)
            } ?: onError(Exception("파일을 열 수 없습니다."))
        } catch (e: Exception) {
            onError(e)
        }
    }
}

