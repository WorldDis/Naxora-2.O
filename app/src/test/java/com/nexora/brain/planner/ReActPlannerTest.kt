package com.nexora.brain.planner

import com.nexora.brain.model.AIPlanResponse
import com.nexora.brain.provider.AIProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReActPlannerTest {

    private class FakeProvider(private val response: AIPlanResponse) : AIProvider {
        override val providerId: String = "FAKE"
        var lastContext: String = ""

        override suspend fun generatePlan(
            systemPrompt: String,
            userQuery: String,
            availableToolsJson: String,
            currentContextState: String
        ): AIPlanResponse {
            lastContext = currentContextState
            return response
        }
    }

    @Test
    fun mapsProviderToolChoiceToPlannerResponse() = runBlocking {
        val provider = FakeProvider(
            AIPlanResponse(
                reasoning = "Open WhatsApp",
                selectedTool = "open_app",
                toolParameters = mapOf("packageName" to "com.whatsapp"),
                isTaskComplete = false,
                finalResponseToUser = "WhatsApp kholchi"
            )
        )
        val planner = ReActPlanner(provider)

        val result = planner.planNextStep("whatsapp khol", "[]", "- [clickable] Chats", emptyList())

        assertEquals("open_app", result.selectedTool)
        assertEquals("com.whatsapp", result.toolParameters["packageName"])
        assertEquals("WhatsApp kholchi", result.finalResponseToUser)
        assertEquals(false, result.isTaskComplete)
    }

    @Test
    fun nullToolParametersBecomeEmptyMap() = runBlocking {
        val provider = FakeProvider(
            AIPlanResponse("done", null, null, true, "Hoye gechhe")
        )
        val result = ReActPlanner(provider).planNextStep("q", "[]", "", emptyList())

        assertNull(result.selectedTool)
        assertTrue(result.toolParameters.isEmpty())
        assertTrue(result.isTaskComplete)
    }

    @Test
    fun includesStepHistoryInContext() = runBlocking {
        val provider = FakeProvider(AIPlanResponse("", null, null, true, null))
        val planner = ReActPlanner(provider)

        planner.planNextStep(
            "scroll koro",
            "[]",
            "- [clickable] Settings",
            listOf("scroll_screen: Successfully scrolled")
        )

        assertTrue(provider.lastContext.contains("Steps already done"))
        assertTrue(provider.lastContext.contains("scroll_screen: Successfully scrolled"))
        assertTrue(provider.lastContext.contains("- [clickable] Settings"))
    }
}
