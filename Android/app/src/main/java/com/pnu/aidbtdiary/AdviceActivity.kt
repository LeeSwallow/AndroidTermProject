package com.pnu.aidbtdiary

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var openAiClient: OpenAiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdviceBinding.inflate(layoutInflater)
        db = AppDatabaseHelper.getDatabase(this)
        dao = db.dbtDiaryDao()
        openAiClient = OpenAiClient()

        setContentView(binding.root)
        initDebug()
        // EntryActivity에서 전달된 데이터
        val dbtDiaryForm = DbtDiaryForm(LocalDate.now())
        dbtDiaryForm.situation = intent.getStringExtra("situation") ?: ""
        dbtDiaryForm.emotion = intent.getStringExtra("emotionType") ?: ""
        dbtDiaryForm.intensity = intent.getIntExtra("intensity", -1)
        dbtDiaryForm.thought = intent.getStringExtra("thought") ?: ""
        dbtDiaryForm.behavior = intent.getStringExtra("reaction") ?: ""

         binding.tvAdviceContent.text = "여기에 AI 조언이 표시됩니다."

        binding.btnGetAdvice.setOnClickListener {
            // AI 조언 요청 로직
            // 예시: OpenAI API 호출하여 조언 가져오기
            binding.tvAdviceContent.text = "AI 조언을 가져오는 중..."
            lifecycleScope.launch {
                binding.btnGetAdvice.isEnabled = false
                val advice = getAdviceFromOpenAI(dbtDiaryForm)
                binding.tvAdviceContent.text = advice
                binding.btnGetAdvice.isEnabled = true
            }
        }

        binding.btnSaveResponse.setOnClickListener {
            val dbtSkill = binding.etDbtSkill.text.toString()

            if (dbtSkill.isBlank()) {
                binding.etUserResponse.error = "dbtSkill을 입력하세요."
                return@setOnClickListener
            }

            binding.btnSaveResponse.isEnabled = false

            dbtDiaryForm.dbtSkill = dbtSkill

            val textClassificationHelper = TextClassificationHelper(
                context = this,
                listener = object : TextClassificationHelper.TextResultsListener {
                    override fun onError(error: String) {
                        binding.btnSaveResponse.error = "분석 중 오류 발생: $error"
                        binding.btnSaveResponse.isEnabled = true
                    }
                    override fun onResult(results: List<Category>, inferenceTime: Long) {
                        dbtDiaryForm.sentiment = results[1].score > 0.5f
                        dbtDiaryForm.solution = binding.etUserResponse.text.toString()

                        dbtDiaryForm.toEntity().let { dbtDiary ->
                            lifecycleScope.launch {
                                dao.insert(dbtDiary)
                                Snackbar.make(
                                    binding.root,
                                    "일기가 저장되었습니다.",
                                    Snackbar.LENGTH_LONG
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
        openAiClient.getCompletion(req)?.let { response ->
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
                        Snackbar.make(binding.root, "번역 실패: ${e.message}", Snackbar.LENGTH_LONG).show()
                        callback(form.emotion)
                        translator.close()
                    }
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "모델 다운로드 실패: ${e.message}", Snackbar.LENGTH_LONG).show()
                callback(form.emotion)
                translator.close()
            }
    }

    private fun initDebug() {
        binding.etDbtSkill.setText("마음읽기")
        binding.etUserResponse.setText("상대방의 마음을 이해하려고 노력했어요. 그 사람의 입장에서 생각해보니, 그 사람도 힘든 상황이었을 것 같아요. 그래서 더 이상 화내지 않기로 했어요.")
    }
}

