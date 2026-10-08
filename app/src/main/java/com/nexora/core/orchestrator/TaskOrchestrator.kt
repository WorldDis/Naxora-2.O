package com.nexora.core.orchestrator

import com.nexora.accessibility.NexoraAccessibilityService
import com.nexora.brain.planner.ReActPlanner
import com.nexora.core.eventbus.NexoraEvent
import com.nexora.core.eventbus.NexoraEventBus
import com.nexora.core.state.TaskState
import com.nexora.core.state.TaskStateManager
import com.nexora.safety.RiskGateManager
import com.nexora.tools.ToolRegistry
import com.nexora.tools.ToolResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TaskOrchestrator(
    private val taskStateManager: TaskStateManager,
    private val planner: ReActPlanner,
    private val toolRegistry: ToolRegistry,
    private val riskGate: RiskGateManager,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {

    private var currentJob: Job? = null

    init {
        // collectLatest ব্যবহার করা হয়নি: নিজের emit করা event task-কে cancel করে ফেলত
        scope.launch {
            NexoraEventBus.events.collect { event ->
                when (event) {
                    is NexoraEvent.UserSpokenInput -> {
                        currentJob?.cancel()
                        currentJob = scope.launch { runTask(event.transcript) }
                    }
                    is NexoraEvent.StopRequested -> {
                        currentJob?.cancel()
                        taskStateManager.transitionTo(TaskState.IDLE)
                    }
                    else -> Unit
                }
            }
        }
    }

    private suspend fun runTask(query: String) {
        taskStateManager.transitionTo(TaskState.PLANNING)
        val history = mutableListOf<String>()

        for (step in 1..MAX_STEPS) {
            val plan = planner.planNextStep(
                userQuery = query,
                availableToolsJson = toolRegistry.getToolsJsonSchema(),
                screenContext = describeScreen(),
                history = history
            )

            plan.finalResponseToUser?.takeIf { it.isNotBlank() }?.let {
                NexoraEventBus.emit(NexoraEvent.SpeakFeedback(it))
            }

            val toolName = plan.selectedTool?.takeIf { it.isNotBlank() }
            if (plan.isTaskComplete || toolName == null) {
                taskStateManager.transitionTo(TaskState.COMPLETED)
                return
            }

            val tool = toolRegistry.getTool(toolName)
            if (tool == null) {
                history += "Tool '$toolName' does not exist"
                continue
            }

            taskStateManager.transitionTo(TaskState.EXECUTING)
            if (!riskGate.evaluateAndCanExecute(tool, plan.toolParameters)) {
                history += "User did not allow ${tool.name}"
                taskStateManager.transitionTo(TaskState.PLANNING)
                continue
            }

            val result = tool.execute(plan.toolParameters)
            history += when (result) {
                is ToolResult.Success -> "${tool.name}: ${result.message}"
                is ToolResult.Failure -> "${tool.name} failed: ${result.message}"
            }

            // UI update hote ektu somoy dey
            delay(STEP_DELAY_MS)
            taskStateManager.transitionTo(TaskState.PLANNING)
        }

        NexoraEventBus.emit(NexoraEvent.SpeakFeedback("Kaaj-ta shesh korte parlam na."))
        taskStateManager.transitionTo(TaskState.FAILED)
    }

    private fun describeScreen(): String {
        val service = NexoraAccessibilityService.instance ?: return "Accessibility service off"
        val root = service.rootInActiveWindow ?: return "No active window"
        return service.nodeParser.parseTree(root)
            .take(MAX_ELEMENTS)
            .joinToString("\n") { element ->
                val label = element.text ?: element.contentDescription ?: "(no label)"
                val kind = if (element.isClickable) "clickable" else "text"
                "- [$kind] $label"
            }
    }

    companion object {
        private const val MAX_STEPS = 8
        private const val MAX_ELEMENTS = 60
        private const val STEP_DELAY_MS = 800L
    }
}
