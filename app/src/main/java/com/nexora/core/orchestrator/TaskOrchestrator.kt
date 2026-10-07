package com.nexora.core.orchestrator

import com.nexora.accessibility.NexoraAccessibilityService
import com.nexora.brain.planner.ReActPlanner
import com.nexora.core.eventbus.NexoraEvent
import com.nexora.core.eventbus.NexoraEventBus
import com.nexora.core.state.TaskState
import com.nexora.core.state.TaskStateManager
import com.nexora.core.verification.VerificationEngine
import com.nexora.tools.ToolRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TaskOrchestrator(
    private val taskStateManager: TaskStateManager,
    private val planner: ReActPlanner,
    private val toolRegistry: ToolRegistry,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {

    private val verifier = VerificationEngine()

    init {
        observeEvents()
    }

    private fun observeEvents() {
        coroutineScope.launch {
            NexoraEventBus.events.collectLatest { event ->
                when (event) {
                    is NexoraEvent.UserSpokenInput -> {
                        processUserQuery(event.transcript)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun processUserQuery(query: String) {
        coroutineScope.launch(Dispatchers.IO) {
            taskStateManager.transitionTo(TaskState.PLANNING)

            val rootNode = NexoraAccessibilityService.instance?.rootInActiveWindow
            val screenContext = rootNode?.toString() ?: "Screen node not available"

            val response = planner.planNextStep(
                userQuery = query,
                availableToolsJson = toolRegistry.getToolsJsonSchema(),
                screenContext = screenContext
            )

            if (response.finalResponseToUser != null) {
                NexoraEventBus.emit(NexoraEvent.SpeakFeedback(response.finalResponseToUser))
            }

            if (response.isTaskComplete) {
                taskStateManager.transitionTo(TaskState.COMPLETED)
            } else {
                taskStateManager.transitionTo(TaskState.EXECUTING)
            }
        }
    }
}