package com.pnu.aidbtdiary.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName

data class Candidate(
    val content: Content,
    val index: Int
)

data class UsageMetadata(
    @SerializedName("promptTokenCount") val promptTokenCount: Int,
    @SerializedName("candidatesTokenCount") val candidatesTokenCount: Int,
    @SerializedName("totalTokenCount") val totalTokenCount: Int,
    @SerializedName("thoughtsTokenCount") val thoughtsTokenCount: Int
)

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<Candidate>,
    @SerializedName("usageMetadata") val usageMetadata: UsageMetadata,
) {
    fun getResponseText(): String {
        return candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
    }
}

/*
 "candidates": [
    {
      "content": {
        "parts": [
          {
            "text": "내담자님, 안녕하세요. 감정일기를 작성해주셔서 감사합니다. 오늘 겪으신 어려움과 감정들을 함께 살펴보겠습니다.\n\n1.  **상황과 감정에 공감하며 간단히 요약합니다.**\n    시험 결과를 받아보고 많이 실망하고 슬프셨겠어요. \"나는 항상 실패한다\"는 생각이 들 만큼 마음이 무거우셨을 것 같습니다. 그런 힘든 감정 속에서 혼자만의 시간을 가지며 자신을 돌보려고 하신 노력이 느껴집니다.\n\n2.  **내담자가 시도한 DBT 스킬을 구체적으로 짚어주고 칭찬합니다.**\n    힘든 상황 속에서도 자신의 감정이 '슬픔'임을 명확히 인지하고 그 강도까지 기록하신 것은 정말 훌륭한 **감정 조절(Emotion Regulation)**의 첫걸음입니다. 자신의 감정을 알아차리는 것은 변화를 위한 중요한 시작점입니다. 또한, 감정에 압도되었을 때 혼자만의 공간에서 시간을 보내며 스스로를 보호하려 한 것은 자연스러운 자기 돌봄의 방식이라고 볼 수 있습니다. 스스로의 감정을 인식하고 기록하신 것만으로도 큰 노력과 용기가 필요한 일입니다.\n\n3.  **해당 상황에서 추가로 사용할 수 있는 DBT 스킬을 예시와 함께 제안합니다.**\n    지금처럼 속상하고 슬픈 감정이 들 때, 다음과 같은 DBT 스킬들을 시도해보는 것을 제안합니다.\n\n    *   **마음챙김(Mindfulness) - 생각을 관찰하기:** \"나는 항상 실패한다\"와 같은 부정적인 생각이 들 때, 그 생각에 휩쓸리기보다는 잠시 멈춰 서서 그저 '생각이구나' 하고 관찰해볼 수 있습니다. 예를 들어, \"아, 지금 내가 시험을 망쳐서 '나는 항상 실패한다'는 생각이 떠올랐구나\" 하고 그 생각을 판단 없이 바라보는 연습을 해보세요. 이 연습은 생각이 곧 현실이 아니라는 것을 깨닫는 데 도움이 됩니다.\n\n    *   **감정 조절(Emotion Regulation) - 반대 행동(Opposite Action) 시도하기:** 슬픔은 활동을 줄이고 혼자 있고 싶게 만드는 감정입니다. 이런 슬픔에 대한 '반대 행동'을 시도하는 것이 도움이 될 수 있습니다.\n        *   **활동 늘리기:** 슬플 때 좋아하는 음악을 듣거나, 짧게라도 산책을 나가 햇볕을 쬐는 등 기분 전환이 될 만한 작은 활동을 시도해 보는 건 어떨까요?\n        *   **긍정적 경험 늘리기:** 다음 시험을 위해 구체적인 학습 계획을 세우거나, 이번 시험에서 무엇을 배울 수 있었는지 (예: 시간 관리, 특정 과목 보충 등)를 정리해 보는 것도 미래에 대한 긍정적인 기대를 심어줄 수 있습니다.\n\n    *   **고통 감내(Distress Tolerance) - 자기 위안(Self-soothe):** 너무 슬프고 힘들 때 스스로를 다독여주는 활동을 해볼 수 있습니다.\n        *   따뜻한 차를 마시거나, 좋아하는 향의 로션/비누를 사용하거나, 부드러운 담요를 덮고 편안함을 느껴보는 것처럼 오감을 사용하여 자신을 위로하는 방법을 시도해 보세요.\n\n4.  **내담자의 노력을 칭찬하고, 다양한 스킬 시도를 격려합니다.**\n    내담자님, 오늘 경험하신 슬픔은 결코 작지 않은 감정입니다. 그 감정을 피하지 않고 마주하고 기록하려는 노력 자체가 큰 용기이자 성장을 위한 발판입니다. 오늘 제안 드린 스킬들을 당장 완벽하게 해내지 못해도 괜찮습니다. 중요한 것은 스스로에게 맞는 방법을 찾아보고 시도해보려는 의지입니다. 앞으로도 이렇게 감정들을 기록하고 함께 살펴나가면서, 다양한 DBT 스킬들을 통해 자신을 더 잘 이해하고 건강하게 다루는 방법을 배워나가실 수 있을 것이라 믿습니다. 항상 내담자님의 노력을 지지하고 응원하겠습니다."
          }
        ],
        "role": "model"
      },
      "finishReason": "STOP",
      "index": 0
    }
  ],
  "usageMetadata": {
    "promptTokenCount": 431,
    "candidatesTokenCount": 879,
    "totalTokenCount": 2628,
    "promptTokensDetails": [
      {
        "modality": "TEXT",
        "tokenCount": 431
      }
    ],
    "thoughtsTokenCount": 1318
  },
  "modelVersion": "gemini-2.5-flash",
  "responseId": "yZFWaLSBM7i01MkP9vqK-A8"
}
 */