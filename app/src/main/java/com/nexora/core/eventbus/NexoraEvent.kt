package com.nexora.core.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class NexoraEvent {
    data class UserSpokenInput(val transcript: String) : NexoraEvent()
    data class SpeakFeedback(val text: String) : NexoraEvent()
    data class ErrorOccurred(val message: String) : NexoraEvent()
    data class StopRequested(val reason: String = "") : NexoraEvent()
}

object NexoraEventBus {
    private val _events = MutableSharedFlow<NexoraEvent>(
        extraBufferCapacity = 64
    )

    val events = _events.asSharedFlow()

    fun emit(event: NexoraEvent) {
        _events.tryEmit(event)
    }
}
