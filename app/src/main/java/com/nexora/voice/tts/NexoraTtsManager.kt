package com.nexora.voice.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class NexoraTtsManager(
    context: Context,
    onReady: (Boolean) -> Unit
) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context) { status ->
            isReady = status == TextToSpeech.SUCCESS
            if (isReady) {
                val bn = Locale("bn", "IN")
                val res = tts?.isLanguageAvailable(bn) ?: TextToSpeech.LANG_NOT_SUPPORTED
                tts?.language = if (res >= TextToSpeech.LANG_AVAILABLE) bn else Locale.US
            }
            onReady(isReady)
            Log.d("NexoraTtsManager", "TTS initialized: $isReady")
        }
    }

    fun speak(text: String) {
        if (isReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "nexora-utterance")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        Log.d("NexoraTtsManager", "Shutdown")
    }
}
