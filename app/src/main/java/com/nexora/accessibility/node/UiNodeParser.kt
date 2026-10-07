package com.nexora.accessibility.node

import android.view.accessibility.AccessibilityNodeInfo

data class UiElement(
    val id: String?,
    val text: String?,
    val contentDescription: String?,
    val className: String?,
    val isClickable: Boolean,
    val node: AccessibilityNodeInfo
)

class UiNodeParser {

    fun parseTree(rootNode: AccessibilityNodeInfo?): List<UiElement> {
        val elements = mutableListOf<UiElement>()
        if (rootNode == null) return elements
        
        traverseNode(rootNode, elements)
        return elements
    }

    private fun traverseNode(node: AccessibilityNodeInfo, list: MutableList<UiElement>) {
        val text = node.text?.toString()
        val desc = node.contentDescription?.toString()
        val viewId = node.viewIdResourceName

        if (!text.isNull meOrBlank() || !desc.isNullOrBlank() || node.isClickable) {
            list.add(
                UiElement(
                    id = viewId,
                    text = text,
                    contentDescription = desc,
                    className = node.className?.toString(),
                    isClickable = node.isClickable,
                    node = node
                )
            )
        }

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                traverseNode(child, list)
            }
        }
    }

    fun findNodeByText(rootNode: AccessibilityNodeInfo?, targetText: String): AccessibilityNodeInfo? {
        if (rootNode == null) return null
        val elements = parseTree(rootNode)
        return elements.firstOrNull { element ->
            element.text?.contains(targetText, ignoreCase = true) == true ||
            element.contentDescription?.contains(targetText, ignoreCase = true) == true
        }?.node
    }
}