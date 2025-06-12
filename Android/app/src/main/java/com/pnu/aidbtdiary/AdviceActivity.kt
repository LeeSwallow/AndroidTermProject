package com.pnu.aidbtdiary

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pnu.aidbtdiary.adapter.EmotionAdapter
import com.pnu.aidbtdiary.dao.AppDatabase
import com.pnu.aidbtdiary.dao.DbtDiaryDao
import com.pnu.aidbtdiary.databinding.ActivityAdviceBinding
import com.pnu.aidbtdiary.dto.DbtDiaryForm
import com.pnu.aidbtdiary.helper.AppDatabaseHelper
import com.pnu.aidbtdiary.helper.EnglishTranslationHelper
import com.pnu.aidbtdiary.helper.PromptTemplateHelper
import com.pnu.aidbtdiary.helper.TextClassificationHelper
import com.pnu.aidbtdiary.network.OpenAiClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate

class AdviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdviceBinding

    private lateinit var db: AppDatabase
    private lateinit var dao: DbtDiaryDao

    private lateinit var textClassificationHelper: TextClassificationHelper
    private lateinit var emotionAdapter: EmotionAdapter
    private lateinit var toast: Toast

    private lateinit var translationHelper: EnglishTranslationHelper
    private lateinit var openAiClient: OpenAiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabaseHelper.getDatabase(this)
        dao = db.dbtDiaryDao()
        textClassificationHelper = TextClassificationHelper(this)
        emotionAdapter = EmotionAdapter(this)
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        initDebug()
        val dbtDiaryForm = getDbtFormFromIntent()

        binding.btnGetAdvice.setOnClickListener {
            binding.tvAdviceContent.setText("AI 조언을 가져오는 중...")

            if (::openAiClient.isInitialized.not()) openAiClient = OpenAiClient()
            lifecycleScope.launch {
                try {
                    binding.btnGetAdvice.isEnabled = false
                    val advice = getAdviceFromOpenAI(dbtDiaryForm)
                    binding.tvAdviceContent.text = advice
                } catch (e: Exception) {
                    binding.tvAdviceContent.setText("AI 조언을 가져오는 데 실패했습니다: ${e.message}")
                    binding.btnGetAdvice.isEnabled = true
                }
            }
        }
        binding.btnSaveResponse.setOnClickListener {
            binding.btnSaveResponse.isEnabled = false

            val dbtSkill = binding.etDbtSkill.text.toString()
            val userResponse = binding.etUserResponse.text.toString()

            if (dbtSkill.isBlank()) {
                binding.etUserResponse.error = "dbtSkill을 입력하세요."
                return@setOnClickListener
            }
            dbtDiaryForm.dbtSkill = dbtSkill
            dbtDiaryForm.solution = userResponse
            val targetText = dbtDiaryForm.situation

            lifecycleScope.launch {
                if (isEnglish(targetText)) {
                    dbtDiaryForm.sentiment = textClassificationHelper.classify(targetText)
                } else if (hasKorean(targetText)) {
                    try {
                        if (!::translationHelper.isInitialized) {
                            translationHelper = EnglishTranslationHelper()

                        }
                        if (!translationHelper.checkIfModelDownloaded()) {
                            val dialRes = getAnswerFromDialog()
                            when (dialRes) {
                                ResponseType.CONFIRM -> {
                                    toast.setText("번역 모델을 다운로드 중입니다...")
                                    toast.show()
                                    translationHelper.downloadModel()
                                    toast.cancel()
                                    dbtDiaryForm.sentiment =
                                        textClassificationHelper.classify(dbtDiaryForm.situation)
                                }

                                ResponseType.CANCEL -> {
                                    dbtDiaryForm.sentiment =
                                        emotionAdapter.getSentimentByEmotion(dbtDiaryForm.emotion)
                                }

                                ResponseType.CLOSE -> {
                                    binding.btnSaveResponse.isEnabled = true
                                    return@launch
                                }
                            }
                        }

                    } catch (e: Exception) {
                        dbtDiaryForm.sentiment =
                            emotionAdapter.getSentimentByEmotion(dbtDiaryForm.emotion)
                    }
                }
                dao.insert(dbtDiaryForm.toEntity())
                Toast.makeText(applicationContext, "DBT 일기가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun getDbtFormFromIntent(): DbtDiaryForm {
        val date = LocalDate.now()
        val form = DbtDiaryForm(date)
        form.situation = intent.getStringExtra("situation") ?: ""
        form.emotion = intent.getStringExtra("emotionType") ?: ""
        form.intensity = intent.getIntExtra("intensity", -1)
        form.thought = intent.getStringExtra("thought") ?: ""
        form.behavior = intent.getStringExtra("reaction") ?: ""
        return form
    }

    private fun isEnglish(text: String): Boolean {
        return text.all { it.isLetter() || it.isWhitespace() || it.isDigit() }
    }

    private fun hasKorean(text: String): Boolean {
        return text.any { it in '\uAC00'..'\uD7AF' }
    }

    enum class ResponseType {
        CONFIRM, CANCEL, CLOSE
    }

    private suspend fun getAnswerFromDialog(): ResponseType = suspendCancellableCoroutine { cont ->
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("모델 다운로드 필요")
            .setMessage("좀 더 정확한 감정 분석을 위해 번역 모델을 다운로드해야 합니다. 다운로드하시겠습니까?")
            .setPositiveButton("다운로드") { _, _ -> cont.resume(ResponseType.CONFIRM, onCancellation = null) }
            .setNegativeButton("그냥 저장") { _, _ -> cont.resume(ResponseType.CANCEL, onCancellation = null) }
            .setOnCancelListener { cont.resume(ResponseType.CLOSE, onCancellation = null) }
            .show()
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

    private fun initDebug() {
        binding.etDbtSkill.setText("마음읽기")
        binding.etUserResponse.setText("상대방의 마음을 이해하려고 노력했어요. 그 사람의 입장에서 생각해보니, 그 사람도 힘든 상황이었을 것 같아요. 그래서 더 이상 화내지 않기로 했어요.")
    }
}
