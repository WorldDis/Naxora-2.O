package com.nexora.core.eventbus

import com.nexora.core.state.TaskState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class NexoraEvent {
    data class UserSpokenInput(val transcript: String) : NexoraEvent()
    data class SpeakFeedback(val text: String) : NexoraEvent()
    data class StateChanged(val oldState: TaskState, val newState: TaskState) : NexoraEvent()
    data class ErrorOccurred(val message: String) : NexoraEvent()
    data class StopRequested(val reason: String = "") : NexoraEvent()
}

object NexoraEventBus {
    private val _events = MutableSharedFlow<NexoraEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<NexoraEvent> = _events.asSharedFlow()

    fun emit(event: NexoraEvent) {
        _events.tryEmit(event)
    }

    suspend fun publish(event: NexoraEvent) {
        _events.emit(event)
    }
}
