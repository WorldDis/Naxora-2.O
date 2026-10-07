package com.nexora.safety

import com.nexora.tools.NexoraTool

interface UserConfirmationCallback {
    suspend fun requestConfirmation(
        tool: NexoraTool,
        parameters: Map<String, Any?>,
        explanation: String
    ): Boolean
}