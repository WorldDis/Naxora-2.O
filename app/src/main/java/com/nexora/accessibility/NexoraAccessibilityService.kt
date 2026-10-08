package com.nexora.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
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

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    companion object {
        var instance: NexoraAccessibilityService? = null
            private set
    }
}
