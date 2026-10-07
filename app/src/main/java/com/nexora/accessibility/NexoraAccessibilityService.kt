package com.nexora.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.nexora.accessibility.actions.AccessibilityActionDispatcher
import com.nexora.accessibility.node.UiNodeParser

class NexoraAccessibilityService : AccessibilityService() {

    lateinit var nodeParser: UiNodeParser
        private set
    lateinit var actionDispatcher: AccessibilityActionDispatcher
        private set

    override fun onServiceConnected() {
        super.onServiceConnected()
        nodeParser = UiNodeParser()
        actionDispatcher = AccessibilityActionDispatcher(this)
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Active window changes observed here
    }

    override fun onInterrupt() {}

    companion object {
        var instance: NexoraAccessibilityService? = null
            private set
    }
}