package com.nexora.core.state

import androidx.annotation.MainThread

class TaskStateManager {
    @Volatile
    private var currentState: TaskState = TaskState.IDLE

    @MainThread
    fun transitionTo(state: TaskState) {
        currentState = state
    }

    fun getCurrentState(): TaskState = currentState

    fun reset() {
        currentState = TaskState.IDLE
    }
}
