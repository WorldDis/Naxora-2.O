package com.nexora.safety

import com.nexora.core.eventbus.NexoraEvent
import com.nexora.core.eventbus.NexoraEventBus
import com.nexora.tools.NexoraTool

/**
 * Confirmation UI ekhono nei, tai high-risk kaaj ekhon sobsomoy cancel hobe.
 * Pore voice ba dialog diye confirm korar implementation add korte hobe.
 */
class DenyAllConfirmation : UserConfirmationCallback {

    override suspend fun requestConfirmation(
        tool: NexoraTool,
        parameters: Map<String, Any?>,
        explanation: String
    ): Boolean {
        NexoraEventBus.emit(
            NexoraEvent.SpeakFeedback("Ei kaaj-er jonno tomar confirmation lagbe, tai ami bondho korchi.")
        )
        return false
    }
}
