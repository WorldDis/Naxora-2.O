package com.nexora.voice.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class NexoraTtsManager(
    context: Context,
    private val onInitComplete: (Boolean) -> Unit
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            isInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {}
                override fun onError(utteranceId: String?) {
                    Log.e("NexoraTTS", "Error synthesized speech utterance: $utteranceId")
                }
            })
            onInitComplete(isInitialized)
        } else {
            Log.e("NexoraTTS", "Initialization failed with status code: $status")
            onInitComplete(false)
        }
    }

    fun speak(text: String, languageCode: String = "en") {
        if (!isInitialized) return

        val locale = when (languageCode.lowercase()) {
            "bn", "bengali" -> Locale("bn", "IN")
            else -> Locale.US
        }

        tts?.language = locale
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NEXORA_UTTERANCE_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}