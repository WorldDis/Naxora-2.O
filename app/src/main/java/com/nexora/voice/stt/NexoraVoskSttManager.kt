package com.nexora.voice.stt

import android.content.Context
import android.util.Log

class NexoraVoskSttManager(
    context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private var isListening = false

    fun initializeModel() {
        Log.d("NexoraVoskSttManager", "Initializing Vosk STT model")
    }

    fun startListening() {
        isListening = true
        Log.d("NexoraVoskSttManager", "Started listening")
    }

    fun stopListening() {
        isListening = false
        Log.d("NexoraVoskSttManager", "Stopped listening")
    }

    fun destroy() {
        stopListening()
        Log.d("NexoraVoskSttManager", "Destroyed")
    }
}
