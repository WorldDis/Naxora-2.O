package com.nexora.core.state

enum class TaskState {
    IDLE,
    LISTENING,
    UNDERSTANDING,
    PLANNING,
    EXECUTING,
    WAITING,
    VERIFYING,
    RETRYING,
    WAITING_FOR_CONFIRMATION,
    COMPLETED,
    FAILED,
    CANCELLED
}
