package com.nexora.tools.system

import android.view.accessibility.AccessibilityNodeInfo
import com.nexora.accessibility.NexoraAccessibilityService
import com.nexora.tools.NexoraTool
import com.nexora.tools.RiskTier
import com.nexora.tools.ToolResult

class ScrollTool : NexoraTool {

    override val name: String = "scroll_screen"
    override val description: String = "Scrolls the current screen directionally (FORWARD/BACKWARD)."
    override val riskLevel: RiskTier = RiskTier.LOW

    override suspend fun execute(parameters: Map<String, Any?>): ToolResult {
        val direction = (parameters["direction"] as? String)?.uppercase() ?: "FORWARD"

        val service = NexoraAccessibilityService.instance
            ?: return ToolResult.Failure("Accessibility Service is not running.")

        val rootNode = service.rootInActiveWindow
            ?: return ToolResult.Failure("Unable to capture active screen window.")

        val action = if (direction == "FORWARD" || direction == "DOWN") {
            AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
        } else {
            AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
        }

        val success = rootNode.performAction(action)
        return if (success) {
            ToolResult.Success("Successfully scrolled screen: $direction")
        } else {
            ToolResult.Failure("Failed to perform scroll action.")
        }
    }
}