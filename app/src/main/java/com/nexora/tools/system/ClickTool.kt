package com.nexora.tools.system

import com.nexora.accessibility.NexoraAccessibilityService
import com.nexora.tools.NexoraTool
import com.nexora.tools.RiskTier
import com.nexora.tools.ToolResult

class ClickTool : NexoraTool {

    override val name: String = "click_element"
    override val description: String = "Clicks on a UI element specified by its text or content description."
    override val riskLevel: RiskTier = RiskTier.LOW

    override suspend fun execute(parameters: Map<String, Any?>): ToolResult {
        val targetText = parameters["targetText"] as? String
            ?: return ToolResult.Failure("Missing parameter: targetText")

        val service = NexoraAccessibilityService.instance
            ?: return ToolResult.Failure("Accessibility Service is not running.")

        val rootNode = service.rootInActiveWindow
            ?: return ToolResult.Failure("Unable to capture active screen window.")

        val targetNode = service.nodeParser.findNodeByText(rootNode, targetText)
            ?: return ToolResult.Failure("Element containing text '$targetText' not found on screen.")

        val success = service.actionDispatcher.performClick(targetNode)
        return if (success) {
            ToolResult.Success("Clicked on element: '$targetText'")
        } else {
            ToolResult.Failure("Failed to perform click action on: '$targetText'")
        }
    }
}