package com.pnu.aidbtdiary

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pnu.aidbtdiary.dto.TTSRequest
import com.pnu.aidbtdiary.network.OpenAiClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OpenAPITest {
    val client = OpenAiClient()
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun testTTS() {
        val req = TTSRequest(
            input = "안녕하세요, AI DBT 다이어리입니다.",
            instructions = "이 문장을 자연스럽게 읽어주세요.",
            model = "gpt-4o-mini-tts",
            voice = "coral"
        )

        GlobalScope.launch {
            val request = client.getSpeech(req)
            if (request is ResponseBody) {
                val audioBytes = request.bytes()
                // 여기서 audioBytes를 파일로 저장하거나 다른 처리를 할 수 있습니다.
                println("Audio bytes received: ${audioBytes.size} bytes")
                assert(audioBytes.isNotEmpty()) { "Audio bytes should not be empty" }
            }
        }
    }
}