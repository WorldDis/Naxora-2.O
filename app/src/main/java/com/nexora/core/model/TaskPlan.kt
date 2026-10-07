package com.nexora.core.model

import com.nexora.tools.RiskTier

/**
 * AI Brain দ্বারা তৈরি করা পুরো প্ল্যান এবং প্রতিটি ধাপের (Step) ডাটা মডেল।
 */

// একটি পুরো কাজের প্ল্যান
data class TaskPlan(
    val originalQuery: String,
    val steps: List<TaskStep>,
    val currentStepIndex: Int = 0
) {
    fun isCompleted(): Boolean = currentStepIndex >= steps.size

    fun getCurrentStep(): TaskStep? = steps.getOrNull(currentStepIndex)
}

// প্ল্যানের প্রতিটি আলাদা পদক্ষেপ (Step)
data class TaskStep(
    val stepId: Int,
    val description: String,
    val toolName: String,
    val toolParameters: Map<String, Any?>,
    val riskLevel: RiskTier = RiskTier.LOW,
    var isVerified: Boolean = false
)