package com.pnu.aidbtdiary.dto


data class Part(
    val text: String
)

data class SystemInstruction (
    val parts: List<Part>
)

data class Content(
    val parts: List<Part>
)

data class GeminiRequest(
    val system_instruction: SystemInstruction,
    val contents: List<Content>
)