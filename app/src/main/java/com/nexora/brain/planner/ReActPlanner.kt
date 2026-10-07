package com.nexora.brain.planner

data class PlannerResponse(
    val finalResponseToUser: String?,
    val isTaskComplete: Boolean
)

class ReActPlanner {
    fun planNextStep(
        userQuery: String,
        availableToolsJson: String,
        screenContext: String
    ): PlannerResponse {
        // Placeholder implementation
        return PlannerResponse(
            finalResponseToUser = "Processing: $userQuery",
            isTaskComplete = false
        )
    }
}
