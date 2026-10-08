package com.nexora.tools.system

import android.view.accessibility.AccessibilityNodeInfo
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

        // Label-er text node na, nijer editable field-tai khunjchhi
        val targetNode = if (fieldLabel != null) {
            findEditable(rootNode, fieldLabel) ?: rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        } else {
            rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT) ?: findEditable(rootNode, null)
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

    private fun findEditable(node: AccessibilityNodeInfo?, label: String?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isEditable && (label == null || matchesLabel(node, label))) return node
        for (i in 0 until node.childCount) {
            findEditable(node.getChild(i), label)?.let { return it }
        }
        return null
    }

    private fun matchesLabel(node: AccessibilityNodeInfo, label: String): Boolean {
        val candidates = listOfNotNull(
            node.text?.toString(),
            node.contentDescription?.toString(),
            node.hintText?.toString()
        )
        return candidates.any { it.contains(label, ignoreCase = true) }
    }
}
