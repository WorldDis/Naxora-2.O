package com.nexora.safety

import com.nexora.tools.NexoraTool
import com.nexora.tools.RiskTier

class RiskGateManager(private val confirmationCallback: UserConfirmationCallback) {

    /**
     * টুল এক্সিকিউট করার পূর্বে রিস্ক লেভেল যাচাই করে
     */
    suspend fun evaluateAndCanExecute(
        tool: NexoraTool,
        parameters: Map<String, Any?>
    ): Boolean {
        return when (tool.riskLevel) {
            RiskTier.LOW -> true
            RiskTier.MEDIUM, RiskTier.HIGH, RiskTier.CRITICAL -> {
                val explanation = "Tool '${tool.name}' requires authorization due to elevated risk tier (${tool.riskLevel})."
                confirmationCallback.requestConfirmation(tool, parameters, explanation)
            }
        }
    }
}