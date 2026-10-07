package com.nexora.brain.model

data class AiResponse(
    val message: String,
    val action: String?,
    val parameters: Map<String, Any?>?
)
