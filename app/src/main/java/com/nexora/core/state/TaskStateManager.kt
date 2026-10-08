package com.nexora.core.state

import com.nexora.core.eventbus.NexoraEvent
import com.nexora.core.eventbus.NexoraEventBus

class TaskStateManager {
    @Volatile
    private var currentState: TaskState = TaskState.IDLE

    fun transitionTo(state: TaskState) {
        val old = currentState
        if (old == state) return
        currentState = state
        NexoraEventBus.emit(NexoraEvent.StateChanged(old, state))
    }

    fun getCurrentState(): TaskState = currentState

    fun reset() {
        transitionTo(TaskState.IDLE)
    }
}
