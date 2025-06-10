package com.pnu.aidbtdiary

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.pnu.aidbtdiary.dao.AppDatabase
import com.pnu.aidbtdiary.dao.DbtDiaryDao
import com.pnu.aidbtdiary.databinding.ActivityAdviceBinding
import com.pnu.aidbtdiary.dto.DbtDiaryForm
import com.pnu.aidbtdiary.helper.AppDatabaseHelper
import com.pnu.aidbtdiary.helper.PromptTemplateHelper
import com.pnu.aidbtdiary.helper.TextClassificationHelper
import com.pnu.aidbtdiary.network.OpenAiClient
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.label.Category
import java.time.LocalDate

class AdviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdviceBinding
    private lateinit var db: AppDatabase
    private lateinit var dao: DbtDiaryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdviceBinding.inflate(layoutInflater)
        db = AppDatabaseHelper.getDatabase(this)
        dao = db.dbtDiaryDao()

        setContentView(binding.root)

        // EntryActivity에서 전달된 데이터
        val dbtDiaryForm = DbtDiaryForm(LocalDate.now())
        dbtDiaryForm.situation = intent.getStringExtra("situation") ?: ""
        dbtDiaryForm.emotion = intent.getStringExtra("emotionType") ?: ""
        dbtDiaryForm.intensity = intent.getIntExtra("intensity", -1)
        dbtDiaryForm.thought = intent.getStringExtra("thought") ?: ""
        dbtDiaryForm.behavior = intent.getStringExtra("reaction") ?: ""

         binding.tvAdvicePrompt.text = "여기에 AI 조언이 표시됩니다."

        binding.btnGetAdvice.setOnClickListener {
            // AI 조언 요청 로직
            // 예시: OpenAI API 호출하여 조언 가져오기
            binding.tvAdvicePrompt.text = "AI 조언을 가져오는 중..."
            lifecycleScope.launch {
                binding.btnGetAdvice.isEnabled = false
                val advice = getAdviceFromOpenAI(dbtDiaryForm)
                binding.tvAdvicePrompt.text = advice
                binding.btnGetAdvice.isEnabled = true
            }
        }

        binding.btnSaveResponse.setOnClickListener {
            val userResponse = binding.etUserResponse.text.toString()

            if (userResponse.isBlank()) {
                binding.etUserResponse.error = "사용자 대응을 입력해주세요."
                return@setOnClickListener
            }

            binding.btnSaveResponse.isEnabled = false

            // 사용자 대응 저장
            dbtDiaryForm.dbtSkill = userResponse

            val textClassificationHelper = TextClassificationHelper(
                context = this,
                listener = object : TextClassificationHelper.TextResultsListener {
                    override fun onError(error: String) {
                        binding.btnSaveResponse.error = "분석 중 오류 발생: $error"
                        binding.btnSaveResponse.isEnabled = true
                    }
                    override fun onResult(results: List<Category>, inferenceTime: Long) {
                        val sentiment =  results[1].score > 0.5f
                        dbtDiaryForm.toEntityWithSentiment(sentiment).let { dbtDiary ->
                            lifecycleScope.launch {
                                dao.insert(dbtDiary)
                                Toast.makeText(
                                    this@AdviceActivity,
                                    "일기가 저장되었습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish() // 일기 저장 후 Activity 종료
                            }
                        }
                    }
                }
            )
            analysisSentiment(dbtDiaryForm) { translatedText ->
                textClassificationHelper.classify(translatedText)
            }
        }
    }

    private suspend fun getAdviceFromOpenAI(dbtDiaryForm: DbtDiaryForm): String {
        if (!dbtDiaryForm.isValid()) {
            return "입력된 정보가 유효하지 않습니다."
        }
        val req = PromptTemplateHelper.generateCompletionRequest(dbtDiaryForm)
        OpenAiClient.getCompletion(req)?.let { response ->
            if (response.choices.isNotEmpty()) {
                return response.choices[0].message.content
            } else {
                return "AI 조언을 가져오는 데 실패했습니다."
            }
        } ?: run {
            return "AI 조언을 가져오는 데 실패했습니다."
        }
    }

    private fun isEnglish(text: String): Boolean {
        return text.all { it.isLetter() || it.isWhitespace() || it.isDigit() }
    }

    private fun analysisSentiment(form: DbtDiaryForm, callback: (String) -> Unit) {
        if (isEnglish(form.situation)) return callback(form.situation);

        val translator = Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.KOREAN)
                .setTargetLanguage(TranslateLanguage.ENGLISH).build()
        )

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                translator.translate(form.situation)
                    .addOnSuccessListener { translatedText ->
                        callback(translatedText)
                        translator.close()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "번역 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                        callback(form.emotion)
                        translator.close()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "모델 다운로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                callback(form.emotion)
                translator.close()
            }
    }
}

