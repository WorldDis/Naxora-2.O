package com.nexora.tools.system

import com.nexora.accessibility.NexoraAccessibilityService
import com.nexora.tools.NexoraTool
import com.nexora.tools.RiskTier
import com.nexora.tools.ToolResult

class TypeTextTool : NexoraTool {

    override val name: String = "type_text"
    override val description: String = "Types text into a targeted focused or labeled input field."
    override val riskLevel: RiskTier = RiskTier.LOW

    override suspend fun execute(parameters: Map<String, Any?>): ToolResult {
        val textToType = parameters["text"] as? String
            ?: return ToolResult.Failure("Missing parameter: text")
        val fieldLabel = parameters["fieldLabel"] as? String

        val service = NexoraAccessibilityService.instance
            ?: return ToolResult.Failure("Accessibility Service is not running.")

        val rootNode = service.rootInActiveWindow
            ?: return ToolResult.Failure("Unable to capture active screen window.")

        val targetNode = if (fieldLabel != null) {
            service.nodeParser.findNodeByText(rootNode, fieldLabel)
        } else {
            rootNode.findFocus(android.view.accessibility.AccessibilityNodeInfo.FOCUS_INPUT)
        }

        if (targetNode == null) {
            return ToolResult.Failure("No suitable input field found to type text.")
        }

        val success = service.actionDispatcher.performType(targetNode, textToType)
        return if (success) {
            ToolResult.Success("Successfully typed '$textToType'")
        } else {
            ToolResult.Failure("Failed to type text into the targeted field.")
        }
    }
}