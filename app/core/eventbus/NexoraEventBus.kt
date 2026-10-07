package com.nexora.core.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

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
