package com.pnu.aidbtdiary

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pnu.aidbtdiary.dto.DbtDiaryForm
import com.pnu.aidbtdiary.helper.PromptTemplateHelper
import com.pnu.aidbtdiary.network.GeminiClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class GeminiTest {
    lateinit var testDbtFrom : DbtDiaryForm

    @Before
    fun setUp() {
        testDbtFrom = DbtDiaryForm(LocalDate.parse("2025-06-21"))
        testDbtFrom.situation = "시험을 망쳤다"
        testDbtFrom.emotion = "슬픔"
        testDbtFrom.intensity = 5
        testDbtFrom.thought = "나는 항상 실패한다"
        testDbtFrom.behavior = "혼자 방에 틀어박혀 있었다"
    }


    @Test
    fun testGemini() = runBlocking {
        val api = GeminiClient()
        val req = PromptTemplateHelper.generatorGeminiRequest(testDbtFrom)
        println("Gemini Request: $req")

        try {
            val response = api.getCompletion(req)
            println("Gemini Response: $response")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}