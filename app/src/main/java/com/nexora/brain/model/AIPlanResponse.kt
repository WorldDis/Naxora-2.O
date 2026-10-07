package com.nexora.brain.model

data class AIPlanResponse(
    val reasoning: String,
    val selectedTool: String?,
    val toolParameters: Map<String, Any?>?,
    val isTaskComplete: Boolean,
    val finalResponseToUser: String?
)