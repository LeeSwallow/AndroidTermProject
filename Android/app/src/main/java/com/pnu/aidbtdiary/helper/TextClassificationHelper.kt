package com.pnu.aidbtdiary.helper

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier

class TextClassificationHelper(
    val context: Context,
) {
    private val model = "mobilebert.tflite"
    private lateinit var bertClassifier: BertNLClassifier

    init {
        initClassifier()
    }

    fun initClassifier() {
        val baseOptionsBuilder = BaseOptions.builder()
        val baseOptions = baseOptionsBuilder.build()
        val options = BertNLClassifier.BertNLClassifierOptions
            .builder()
            .setBaseOptions(baseOptions)
            .build()
        bertClassifier = BertNLClassifier.createFromFileAndOptions(context, model,options)
    }

    suspend fun classify(text: String): Boolean {
        val results = withContext(Dispatchers.IO) {
            bertClassifier.classify(text.trim())
        }
        return results[1].score > results[0].score
    }
}