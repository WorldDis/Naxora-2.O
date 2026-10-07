package com.nexora.core.verification

import android.view.accessibility.AccessibilityNodeInfo
import com.nexora.accessibility.node.UiNodeParser

class VerificationEngine {

    private val nodeParser = UiNodeParser()

    /**
     * ক্লিক বা টাইপ করার পর স্ক্রিনে কাঙ্ক্ষিত টেক্সট বা নোড এসেছে কিনা যাচাই করে
     */
    fun verifyScreenContainsText(rootNode: AccessibilityNodeInfo?, expectedText: String): Boolean {
        if (rootNode == null) return false
        val node = nodeParser.findNodeByText(rootNode, expectedText)
        return node != null
    }

    /**
     * স্ক্রিনের স্টেট পরিবর্তন হয়েছে কিনা তা নোড সংখ্যা তুলনা করে নিশ্চিত করে
     */
    fun verifyStateChanged(previousNodeCount: Int, currentNodeCount: Int): Boolean {
        return previousNodeCount != currentNodeCount
    }
}