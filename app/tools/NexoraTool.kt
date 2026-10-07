package com.nexora.tools

interface NexoraTool {
    val name: String
    val description: String
    val riskLevel: RiskTier

    suspend fun execute(params: Map<String, Any?>): ToolResult
}
