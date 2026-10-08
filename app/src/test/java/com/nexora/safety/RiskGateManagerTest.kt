package com.nexora.safety

import com.nexora.tools.NexoraTool
import com.nexora.tools.RiskTier
import com.nexora.tools.ToolResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RiskGateManagerTest {

    private class FakeTool(override val riskLevel: RiskTier) : NexoraTool {
        override val name: String = "fake_$riskLevel"
        override val description: String = "test tool"
        override suspend fun execute(parameters: Map<String, Any?>): ToolResult =
            ToolResult.Success("ok")
    }

    private class RecordingConfirmation(private val answer: Boolean) : UserConfirmationCallback {
        var asked = 0
        override suspend fun requestConfirmation(
            tool: NexoraTool,
            parameters: Map<String, Any?>,
            explanation: String
        ): Boolean {
            asked++
            return answer
        }
    }

    @Test
    fun lowRiskToolRunsWithoutAsking() = runBlocking {
        val confirmation = RecordingConfirmation(answer = false)
        val gate = RiskGateManager(confirmation)

        assertTrue(gate.evaluateAndCanExecute(FakeTool(RiskTier.LOW), emptyMap()))
        assertTrue(confirmation.asked == 0)
    }

    @Test
    fun highRiskToolAsksAndHonoursDenial() = runBlocking {
        val confirmation = RecordingConfirmation(answer = false)
        val gate = RiskGateManager(confirmation)

        assertFalse(gate.evaluateAndCanExecute(FakeTool(RiskTier.HIGH), emptyMap()))
        assertTrue(confirmation.asked == 1)
    }

    @Test
    fun denyAllConfirmationBlocksMediumRiskTools() = runBlocking {
        val gate = RiskGateManager(DenyAllConfirmation())

        assertFalse(gate.evaluateAndCanExecute(FakeTool(RiskTier.MEDIUM), emptyMap()))
        assertFalse(gate.evaluateAndCanExecute(FakeTool(RiskTier.CRITICAL), emptyMap()))
    }
}
