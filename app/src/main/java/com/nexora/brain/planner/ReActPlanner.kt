package com.nexora.brain.planner

import com.nexora.brain.model.AIPlanResponse
import com.nexora.brain.provider.AIProvider

class ReActPlanner(private val aiProvider: AIProvider) {

    private val systemPrompt = """
        You are NEXORA, an autonomous AI Accessibility Agent for Android.
        Your job is to break down user requests into discrete, single-step system tools.
        Observe the screen, reason about the step, and emit structured tool executions.
    """.trimIndent()

    suspend fun planNextStep(
        userQuery: String,
        availableToolsJson: String,
        screenContext: String
    ): AIPlanResponse {
        return aiProvider.generatePlan(
            systemPrompt = systemPrompt,
            userQuery = userQuery,
            availableToolsJson = availableToolsJson,
            currentContextState = screenContext
        )
    }
}