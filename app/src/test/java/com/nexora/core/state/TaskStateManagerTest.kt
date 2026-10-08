package com.nexora.core.state

import org.junit.Assert.assertEquals
import org.junit.Test

class TaskStateManagerTest {

    @Test
    fun startsIdle() {
        assertEquals(TaskState.IDLE, TaskStateManager().getCurrentState())
    }

    @Test
    fun transitionUpdatesState() {
        val manager = TaskStateManager()
        manager.transitionTo(TaskState.PLANNING)
        assertEquals(TaskState.PLANNING, manager.getCurrentState())
        manager.transitionTo(TaskState.EXECUTING)
        assertEquals(TaskState.EXECUTING, manager.getCurrentState())
    }

    @Test
    fun resetReturnsToIdle() {
        val manager = TaskStateManager()
        manager.transitionTo(TaskState.FAILED)
        manager.reset()
        assertEquals(TaskState.IDLE, manager.getCurrentState())
    }
}
