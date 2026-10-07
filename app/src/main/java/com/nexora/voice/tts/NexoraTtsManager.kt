package com.nexora.voice.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log

class NexoraTtsManager(
    context: Context,
    onReady: (Boolean) -> Unit
) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context) { status ->
            isReady = status == TextToSpeech.SUCCESS
            onReady(isReady)
            Log.d("NexoraTtsManager", "TTS initialized: $isReady")
        }
    }

    fun speak(text: String) {
        if (isReady && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    fun stop() {
        if (tts != null) {
            tts?.stop()
        }
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        Log.d("NexoraTtsManager", "Shutdown")
    }
}
