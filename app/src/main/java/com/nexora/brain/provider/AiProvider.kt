package com.nexora.brain.provider

import com.nexora.brain.model.AiResponse

interface AiProvider {
    suspend fun query(prompt: String): AiResponse
}

class LocalAiProvider : AiProvider {
    override suspend fun query(prompt: String): AiResponse {
        return AiResponse(
            message = "Local AI response to: $prompt",
            action = null,
            parameters = null
        )
    }
}
