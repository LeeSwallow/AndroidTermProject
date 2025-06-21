package com.pnu.aidbtdiary.helper

import com.pnu.aidbtdiary.dto.OpenAiRequest
import com.pnu.aidbtdiary.dto.DbtDiaryForm
import com.pnu.aidbtdiary.dto.Message
import com.pnu.aidbtdiary.BuildConfig
import com.pnu.aidbtdiary.dto.GeminiRequest
import com.pnu.aidbtdiary.dto.SystemInstruction
import com.pnu.aidbtdiary.dto.Part
import com.pnu.aidbtdiary.dto.Content

object PromptTemplateHelper {
    const val TEMPLATE_SYSTEM_PROMPT = """
    당신은 내담자의 감정일기를 분석하고, DBT(변증법적 행동치료) 원칙에 따라 공감적이고 전문적으로 피드백을 제공하는 상담가입니다.  
    내담자의 감정, 행동, 사용한 DBT 스킬을 세심하게 살피고, 긍정적인 강화와 추가적인 스킬 제안을 통해 내담자가 다양한 DBT 전략을 시도하도록 격려하세요.  
    DBT 스킬 예시는 다음과 같습니다:  
    - 마음챙김(Mindfulness): 현재 순간에 집중하고, 자신의 감정과 생각을 판단 없이 관찰하는 연습  
    - 고통감내(Distress Tolerance): 위기 상황에서 감정을 견디고, 상황을 악화시키지 않는 기술 (예: TIPP, ACCEPTS)  
    - 감정조절(Emotion Regulation): 감정을 인식하고, 건강하게 표현하며, 긍정적 경험을 늘리는 기술  
    - 대인관계 효율성(Interpersonal Effectiveness): 자신의 욕구를 효과적으로 전달하고, 관계를 건강하게 유지하는 기술 (예: DEAR MAN, GIVE, FAST)
    
    모든 피드백은 따뜻하고 지지적인 어조로 작성하며, 내담자의 노력을 존중하고 성장 가능성을 강조해야 합니다. 다음과 같은 형식으로 500자 이상, 1000자 이하로 작성하세요:

    1. 상황과 감정에 공감하며 간단히 요약합니다.
    2. 내담자가 시도한 DBT 스킬을 구체적으로 짚어주고 칭찬합니다.
    3. 해당 상황에서 추가로 사용할 수 있는 DBT 스킬을 예시와 함께 제안합니다.
    4. 내담자의 노력을 칭찬하고, 다양한 스킬 시도를 격려합니다.
    """

    const val TEMPLATE_USER_PROMPT = """
    내담자의 감정일기:
    
    날짜 : {date}
    상황: {situation}
    감정: {emotion}
    강도: {intensity}
    생각: {thought}
    행동: {behavior}
    """

    private fun generateUserMessage(dbtDiaryForm: DbtDiaryForm): String {
        val userText =  TEMPLATE_USER_PROMPT
            .replace("{date}", dbtDiaryForm.getDate().toString())
            .replace("{situation}", dbtDiaryForm.situation)
            .replace("{emotion}", dbtDiaryForm.emotion)
            .replace("{intensity}", dbtDiaryForm.intensity.toString())
            .replace("{thought}", dbtDiaryForm.thought)
            .replace("{behavior}", dbtDiaryForm.behavior)
        return return userText.trim()
    }

    fun generateCompletionRequest(dbtDiaryForm: DbtDiaryForm): OpenAiRequest {
        val systemMessage = Message(
            role = "developer",
            content = TEMPLATE_SYSTEM_PROMPT.trim()
        )
        val userMessage = generateUserMessage(dbtDiaryForm)

        return OpenAiRequest(
            model = BuildConfig.OPENAI_MODEL,
            messages = listOf(systemMessage, Message(role="user", content = userMessage))
        )
    }

    fun generatorGeminiRequest(dbtDiaryForm: DbtDiaryForm): GeminiRequest {
        val userMessage = generateUserMessage(dbtDiaryForm)

        return GeminiRequest(
            system_instruction = SystemInstruction( parts = listOf(Part(text = TEMPLATE_SYSTEM_PROMPT)))
            , contents = listOf(Content(parts =
                listOf(Part(text = userMessage))
            ))
        )
    }
}

