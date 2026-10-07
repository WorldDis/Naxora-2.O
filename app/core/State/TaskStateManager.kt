package com.nexora.core.state

import com.nexora.core.eventbus.NexoraEvent
import com.nexora.core.eventbus.NexoraEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskStateManager {

    private val _currentState = MutableStateFlow(TaskState.IDLE)
    val currentState: StateFlow<TaskState> = _currentState.asStateFlow()

    fun transitionTo(newState: TaskState) {
        val oldState = _currentState.value
        if (oldState == newState) return

        _currentState.value = newState
        NexoraEventBus.emit(NexoraEvent.StateChanged(oldState, newState))
    }

    fun reset() {
        transitionTo(TaskState.IDLE)
    }
}
