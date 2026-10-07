package com.nexora.brain.provider

import com.nexora.brain.model.AIPlanResponse

interface AIProvider {
    val providerId: String

    suspend fun generatePlan(
        systemPrompt: String,
        userQuery: String,
        availableToolsJson: String,
        currentContextState: String
    ): AIPlanResponse
}