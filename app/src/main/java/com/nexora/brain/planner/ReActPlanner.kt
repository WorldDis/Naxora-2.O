package com.nexora.brain.planner

import com.nexora.brain.provider.AIProvider

data class PlannerResponse(
    val reasoning: String,
    val selectedTool: String?,
    val toolParameters: Map<String, Any?>,
    val finalResponseToUser: String?,
    val isTaskComplete: Boolean
)

class ReActPlanner(private val provider: AIProvider) {

    suspend fun planNextStep(
        userQuery: String,
        availableToolsJson: String,
        screenContext: String,
        history: List<String>
    ): PlannerResponse {
        val context = buildString {
            appendLine("Screen elements:")
            appendLine(screenContext)
            if (history.isNotEmpty()) {
                appendLine("Steps already done:")
                history.forEach { appendLine("- $it") }
            }
        }

        val plan = provider.generatePlan(
            systemPrompt = SYSTEM_PROMPT,
            userQuery = userQuery,
            availableToolsJson = availableToolsJson,
            currentContextState = context
        )

        return PlannerResponse(
            reasoning = plan.reasoning,
            selectedTool = plan.selectedTool,
            toolParameters = plan.toolParameters ?: emptyMap(),
            finalResponseToUser = plan.finalResponseToUser,
            isTaskComplete = plan.isTaskComplete
        )
    }

    companion object {
        private const val SYSTEM_PROMPT = """
You are NEXORA, a voice-controlled Android accessibility agent.
Each turn you get the user's goal, the list of tools, the current screen elements and the steps already done.
Pick at most ONE tool per turn. Use only tool names from the tool list.
For click_element and type_text, use text that appears exactly in the screen elements.
When the goal is finished, set isTaskComplete to true, selectedTool to null and give a short finalResponseToUser.
Reply to the user in the same language they used (Bengali or English).
"""
    }
}
