package com.pnu.aidbtdiary

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.pnu.aidbtdiary.adapter.EmotionAdapter
import com.pnu.aidbtdiary.dao.AppDatabase
import com.pnu.aidbtdiary.dao.DbtDiaryDao
import com.pnu.aidbtdiary.databinding.ActivityAdviceBinding
import com.pnu.aidbtdiary.dto.DbtDiaryForm
import com.pnu.aidbtdiary.helper.AppDatabaseHelper
import com.pnu.aidbtdiary.helper.EnglishTranslationHelper
import com.pnu.aidbtdiary.helper.PlayAudioHelper
import com.pnu.aidbtdiary.helper.PromptTemplateHelper
import com.pnu.aidbtdiary.helper.TextClassificationHelper
import com.pnu.aidbtdiary.network.GeminiClient
import com.pnu.aidbtdiary.network.OpenAiClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate

class AdviceActivity : BaseActivity() {

    private lateinit var binding: ActivityAdviceBinding
    private lateinit var db: AppDatabase
    private lateinit var dao: DbtDiaryDao
    private lateinit var textClassificationHelper: TextClassificationHelper
    private lateinit var emotionAdapter: EmotionAdapter
    private lateinit var toast: Toast
    private lateinit var translationHelper: EnglishTranslationHelper
    private lateinit var openAiClient: OpenAiClient
    private lateinit var geminiClient: GeminiClient
    private lateinit var playAudioHelper: PlayAudioHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnVoice.isEnabled = false
        setSupportActionBar(binding.toolbar)

        // DB 초기화
        db = AppDatabaseHelper.getDatabase(this)
        dao = db.dbtDiaryDao()
        playAudioHelper = PlayAudioHelper(this)

        // 헬퍼 및 어댑터 초기화
        textClassificationHelper = TextClassificationHelper(this)
        emotionAdapter = EmotionAdapter(this)
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        // Debug용 초기 텍스트
        initDebug()

        // Intent로부터 폼 가져오기
        val dbtDiaryForm = getDbtFormFromIntent()

        // Spinner: DBT 스킬 목록 어댑터 설정
        ArrayAdapter.createFromResource(
            this,
            R.array.dbt_skill_prompt,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDbtSkills.adapter = adapter
        }
        binding.spinnerDbtSkills.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                // 선택된 스킬 설정
                dbtDiaryForm.dbtSkill = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) { /* no-op */ }
        }


        // AI 조언 받기 버튼
        binding.btnGetAdvice.setOnClickListener {
            binding.tvAdviceContent.text = "AI 조언을 가져오는 중..."
            if (!::openAiClient.isInitialized) geminiClient = GeminiClient()
            lifecycleScope.launch {
                try {
                    binding.btnGetAdvice.isEnabled = false
                    val advice = getAdviceFromGemini(dbtDiaryForm)
                    binding.tvAdviceContent.text = advice
                } catch (e: Exception) {
                    binding.tvAdviceContent.text = "AI 조언을 가져오는 데 실패했습니다: ${e.message}"
                    binding.btnGetAdvice.isEnabled = true
                }
            }
        }

        binding.btnVoice.setOnClickListener {
            lifecycleScope.launch {
                if (playAudioHelperIsPlaying()) {
                    playAudioHelper.stop()
                    return@launch
                }
                val adviceText = binding.tvAdviceContent.text.toString()
                if (adviceText.isNotBlank()) {
                    try {
                        if (!::openAiClient.isInitialized) openAiClient = OpenAiClient()
                        val responseBody = openAiClient.getSpeechStream(adviceText)
                        playAudioHelper.playStream(responseBody, sampleRate = 24000)
                    } catch (e: Exception) {
                        Toast.makeText(this@AdviceActivity, "음성 재생 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AdviceActivity, "먼저 AI 조언을 받아주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 내 대응 저장 버튼
        binding.btnSaveResponse.setOnClickListener {
            binding.btnSaveResponse.isEnabled = false
            val userResponse = binding.etUserResponse.text.toString()
            dbtDiaryForm.solution = userResponse

            lifecycleScope.launch {
                try {
                    analyzeSentiment(dbtDiaryForm)
                    dao.insert(dbtDiaryForm.toEntity())
                    Toast.makeText(applicationContext, "DBT 일기가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "DBT 일기 저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.btnSaveResponse.isEnabled = true
                }
                finish()
            }
        }

        // 뒤로 가기
        binding.btnBack.setOnClickListener { finish() }
    }

    // 감정 분석 로직 추출
    private suspend fun analyzeSentiment(form: DbtDiaryForm) {
        val text = form.situation
        form.sentiment = when {
            text.all { it.isLetter() || it.isWhitespace() || it.isDigit() } ->
                textClassificationHelper.classify(text)
            text.any { it in '\uAC00'..'\uD7AF' } -> {
                if (!::translationHelper.isInitialized) translationHelper = EnglishTranslationHelper()
                if (!translationHelper.checkIfModelDownloaded()) {
                    when (getAnswerFromDialog()) {
                        ResponseType.CONFIRM -> {
                            toast.setText("번역 모델 다운로드 중...")
                            toast.show()
                            translationHelper.downloadModel()
                            toast.cancel()
                            toast.setText("번역 모델 다운로드 완료")
                            textClassificationHelper.classify(text)
                        }
                        ResponseType.CANCEL -> emotionAdapter.getSentimentByEmotion(form.emotion)
                        ResponseType.CLOSE -> return
                    }
                } else textClassificationHelper.classify(text)

            }
            else -> emotionAdapter.getSentimentByEmotion(form.emotion)
        }
    }

    private fun getDbtFormFromIntent(): DbtDiaryForm {
        val date = LocalDate.now()
        return DbtDiaryForm(date).apply {
            situation = intent.getStringExtra("situation") ?: ""
            emotion = intent.getStringExtra("emotionType") ?: ""
            intensity = intent.getIntExtra("intensity", -1)
            thought = intent.getStringExtra("thought") ?: ""
            behavior = intent.getStringExtra("reaction") ?: ""
        }
    }

    enum class ResponseType { CONFIRM, CANCEL, CLOSE }

    private suspend fun getAnswerFromDialog(): ResponseType =
        suspendCancellableCoroutine { cont ->
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("모델 다운로드 필요")
                .setMessage("번역 모델을 다운로드하시겠습니까?")
                .setPositiveButton("다운로드") { _, _ -> cont.resume(ResponseType.CONFIRM, null) }
                .setNegativeButton("취소") { _, _ -> cont.resume(ResponseType.CANCEL, null) }
                .setOnCancelListener { cont.resume(ResponseType.CLOSE, null) }
                .show()
        }

    private suspend fun getAdviceFromOpenAI(form: DbtDiaryForm): String {
        if (!form.isValid()) return "입력된 정보가 유효하지 않습니다."
        val req = PromptTemplateHelper.generateCompletionRequest(form)
        return openAiClient.getCompletion(req)
            ?.choices?.firstOrNull()?.message?.content
            ?: "AI 조언을 가져오는 데 실패했습니다."
    }

    private suspend fun getAdviceFromGemini(form: DbtDiaryForm): String {
        if (!form.isValid()) return "입력된 정보가 유효하지 않습니다."
        val req = PromptTemplateHelper.generatorGeminiRequest(form)
        binding.btnVoice.isEnabled = true
        return geminiClient.getCompletion(req).getResponseText()
    }

    private fun initDebug() {
        binding.etUserResponse.setText(
            "상대방의 마음을 이해하려고 노력했어요."
        )
    }
    private fun playAudioHelperIsPlaying(): Boolean {
        return try {
            val field = playAudioHelper.javaClass.getDeclaredField("isPlaying")
            field.isAccessible = true
            field.get(playAudioHelper) as Boolean
        } catch (e: Exception) {
            false
        }
    }
}
