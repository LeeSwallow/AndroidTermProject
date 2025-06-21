package com.pnu.aidbtdiary.helper

import android.adservices.ondevicepersonalization.ModelManager
import android.content.Context
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class EnglishTranslationHelper {
    private val modelManager = RemoteModelManager.getInstance()

    private fun getModel(): TranslateRemoteModel {
        return TranslateRemoteModel.Builder(TranslateLanguage.KOREAN).build()
    }

    suspend fun checkIfModelDownloaded(): Boolean {
        val model = getModel()
        return modelManager.isModelDownloaded(model).await()
    }

    suspend fun downloadModel() {
        val model = getModel()
        val conditions = DownloadConditions.Builder()
//            .requireWifi()
            .build()
        modelManager.download(model, conditions).await()
    }

    suspend fun translateToEnglish(text: String): String {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.KOREAN)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        val translator = Translation.getClient(options)
        try {
            val result = translator.translate(text).await()
            translator.close()
            return result
        } catch (e: Exception) {
            translator.close()
            throw e
        }
    }
}

