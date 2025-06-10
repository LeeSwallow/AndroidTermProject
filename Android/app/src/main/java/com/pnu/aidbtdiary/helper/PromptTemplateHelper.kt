package com.pnu.aidbtdiary.helper

import com.pnu.aidbtdiary.dto.CompletionRequest
import com.pnu.aidbtdiary.dto.DbtDiaryForm
import com.pnu.aidbtdiary.dto.Message
import com.pnu.aidbtdiary.BuildConfig

/*
날짜: 2024-06-10
상황: 친구와의 약속이 갑자기 취소되어 혼자 집에 있게 됨
감정: 서운함, 외로움
강도: 7
생각: 나랑 약속 잡는 게 별로였나?
행동: 방에서 음악을 들으며 시간을 보냄
사용한 DBT 스킬: 마음챙김

---

날짜: 2024-06-11
상황: 회사에서 실수를 해서 상사에게 지적을 받음
감정: 불안, 창피함
강도: 8
생각: 내가 너무 부족한가?
행동: 화장실에서 잠시 혼자 마음을 가라앉힘
사용한 DBT 스킬: 디스트랙트(주의 전환)

---

날짜: 2024-06-12
상황: 가족과 저녁 식사 중 의견 충돌이 있었음
감정: 짜증, 답답함
강도: 6
생각: 내 말은 왜 안 통할까?
행동: 잠시 방에 들어가 심호흡을 함
사용한 DBT 스킬: 호흡 조절, 감정 인식
 */

object PromptTemplateHelper {
    const val TEMPLATE_SYSTEM_PROMPT = """
    당신은 내담자의 감정일기를 분석하고, DBT(변증법적 행동치료) 원칙에 따라 공감적이고 전문적으로 피드백을 제공하는 상담가입니다.  
    내담자의 감정, 행동, 사용한 DBT 스킬을 세심하게 살피고, 긍정적인 강화와 추가적인 스킬 제안을 통해 내담자가 다양한 DBT 전략을 시도하도록 격려하세요.  
    DBT 스킬 예시는 다음과 같습니다:  
    - 마음챙김(Mindfulness): 현재 순간에 집중하고, 자신의 감정과 생각을 판단 없이 관찰하는 연습  
    - 고통감내(Distress Tolerance): 위기 상황에서 감정을 견디고, 상황을 악화시키지 않는 기술 (예: TIPP, ACCEPTS)  
    - 감정조절(Emotion Regulation): 감정을 인식하고, 건강하게 표현하며, 긍정적 경험을 늘리는 기술  
    - 대인관계 효율성(Interpersonal Effectiveness): 자신의 욕구를 효과적으로 전달하고, 관계를 건강하게 유지하는 기술 (예: DEAR MAN, GIVE, FAST)
    
    모든 피드백은 따뜻하고 지지적인 어조로 작성하며, 내담자의 노력을 존중하고 성장 가능성을 강조해야 합니다. 최소 1개 이상의 DBT 스킬을 설명과 함께 제안해주세요
    """

    const val TEMPLATE_USER_PROMPT = """
    내담자가 감정일기에 한 가지 상황(trigger)과 감정, 행동, 사용한 DBT 스킬을 기록했습니다.
    다음과 같은 형식으로 피드백을 제공하세요.

    1. 상황과 감정에 공감하며 간단히 요약합니다.
    2. 내담자가 시도한 DBT 스킬을 구체적으로 짚어주고 칭찬합니다.
    3. 해당 상황에서 추가로 사용할 수 있는 DBT 스킬을 예시와 함께 제안합니다.
    4. 내담자의 노력을 칭찬하고, 다양한 스킬 시도를 격려합니다.
    
    예시:
    
    회사에서 실수 후 불안하고 창피한 감정을 느꼈군요. 누구나 그런 상황에서 비슷한 감정을 경험할 수 있어요.
    잠시 자리를 벗어나 마음을 가라앉히는 디스트랙트(주의 전환) 스킬을 잘 사용하셨네요. 스스로를 진정시키려는 노력이 인상적입니다.
    추가로 '호흡 조절'이나 '현실 검증하기' 스킬도 도움이 될 수 있어요. 예를 들어, 천천히 숨을 쉬거나, 실수가 모두에게 일어날 수 있음을 떠올려보는 것도 좋겠어요.
    자신의 감정을 인식하고 대처한 점을 칭찬합니다. 앞으로도 다양한 DBT 스킬을 시도해보세요!

    내담자의 감정일기:
    
    날짜 : {date}
    상황: {situation}
    감정: {emotion}
    강도: {intensity}
    생각: {thought}
    행동: {behavior}
    """

    private fun generateUserMessage(dbtDiaryForm: DbtDiaryForm): Message {
        val userText =  TEMPLATE_USER_PROMPT
            .replace("{date}", dbtDiaryForm.getDate().toString())
            .replace("{situation}", dbtDiaryForm.situation)
            .replace("{emotion}", dbtDiaryForm.emotion)
            .replace("{intensity}", dbtDiaryForm.intensity.toString())
            .replace("{thought}", dbtDiaryForm.thought)
            .replace("{behavior}", dbtDiaryForm.behavior)
        return Message(
            role = "user",
            content = userText.trim()
        )
    }

    fun generateCompletionRequest(dbtDiaryForm: DbtDiaryForm): CompletionRequest {

        val systemMessage = Message(
            role = "developer",
            content = TEMPLATE_SYSTEM_PROMPT.trim()
        )
        val userMessage = generateUserMessage(dbtDiaryForm)

        return CompletionRequest(
            model = BuildConfig.OPENAI_MODEL,
            messages = listOf(systemMessage, userMessage)
        )
    }
}