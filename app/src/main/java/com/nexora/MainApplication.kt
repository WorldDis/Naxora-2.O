package com.nexora

import android.app.Application
import com.nexora.brain.planner.ReActPlanner
import com.nexora.brain.provider.GeminiAIProvider
import com.nexora.core.orchestrator.TaskOrchestrator
import com.nexora.core.state.TaskStateManager
import com.nexora.safety.DenyAllConfirmation
import com.nexora.safety.RiskGateManager
import com.nexora.tools.ToolRegistry
import com.nexora.tools.system.ClickTool
import com.nexora.tools.system.OpenAppTool
import com.nexora.tools.system.ScrollTool
import com.nexora.tools.system.TypeTextTool

class MainApplication : Application() {

    val taskStateManager: TaskStateManager by lazy { TaskStateManager() }

    val toolRegistry: ToolRegistry by lazy {
        ToolRegistry().apply {
            registerTool(ClickTool())
            registerTool(ScrollTool())
            registerTool(TypeTextTool())
            registerTool(OpenAppTool(this@MainApplication))
        }
    }

    val orchestrator: TaskOrchestrator by lazy {
        TaskOrchestrator(
            taskStateManager = taskStateManager,
            planner = ReActPlanner(GeminiAIProvider(BuildConfig.GEMINI_API_KEY)),
            toolRegistry = toolRegistry,
            riskGate = RiskGateManager(DenyAllConfirmation())
        )
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Orchestrator event observe kora shuru kore
        orchestrator
    }

    companion object {
        lateinit var instance: MainApplication
            private set
    }
}
