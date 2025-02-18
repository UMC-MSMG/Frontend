package com.umc_msmg.frontend.interfaces

data class ChatRequest(
    val model: String = "gpt-4o-mini", // ✅ 사용할 모델 (GPT-4 Turbo)
    val messages: List<ChatMessage>
)

data class ChatMessage(
    val role: String,  // "user" 또는 "system"
    val content: String
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: ChatMessage
)

data class TTSRequest(
    val model: String = "tts-1", // ✅ 사용할 모델 (tts-1 / tts-1-hd)
    val input: String,
    val voice: String = "alloy"// ✅ 변환할 텍스트
)

