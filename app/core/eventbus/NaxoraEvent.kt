package com.nexora.core.eventbus

import com.nexora.core.state.TaskState

sealed class NexoraEvent {
    // Voice Events
    data class UserSpokenInput(val transcript: String) : NexoraEvent()
    data class SpeakFeedback(val text: String) : NexoraEvent()

    // State Events
    data class StateChanged(val oldState: TaskState, val newState: TaskState) : NexoraEvent()

    // Task & Tool Events
    data class ToolExecutionRequested(val toolName: String, val params: Map<String, Any?>) : NexoraEvent()
    data class ToolExecutionCompleted(val toolName: String, val isSuccess: Boolean, val message: String?) : NexoraEvent()

    // System Events
    data class ErrorOccurred(val error: String, val throwable: Throwable? = null) : NexoraEvent()
    object StopRequested : NexoraEvent()
}
