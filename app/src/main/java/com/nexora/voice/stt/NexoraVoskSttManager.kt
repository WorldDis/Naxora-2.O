package com.nexora.voice.stt

import android.content.Context
import android.util.Log
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import java.io.IOException

class NexoraVoskSttManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
) : RecognitionListener {

    private var speechService: SpeechService? = null
    private var voskModel: Model? = null

    fun initializeModel(modelPathInAssets: String = "models/vosk-model-small-en-us-0.15") {
        StorageService.unpack(context, modelPathInAssets, "model",
            { model ->
                this.voskModel = model
                Log.d("NexoraSTT", "Vosk Model Loaded Successfully")
            },
            { exception ->
                onError("Failed to unpack Vosk Speech Model: ${exception.localizedMessage}")
            }
        )
    }

    fun startListening() {
        val model = voskModel
        if (model == null) {
            onError("STT Error: Model not initialized yet.")
            return
        }

        try {
            val recognizer = Recognizer(model, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f).apply {
                startListening(this@NexoraVoskSttManager)
            }
        } catch (e: IOException) {
            onError("Failed to start recording audio stream: ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        speechService?.stop()
        speechService?.shutdown()
        speechService = null
    }

    override fun onResult(hypothesis: String?) {
        hypothesis?.let {
            val parsedText = extractTextFromJson(it)
            if (parsedText.isNotBlank()) {
                onResult(parsedText)
            }
        }
    }

    override fun onFinalResult(hypothesis: String?) {
        hypothesis?.let {
            val parsedText = extractTextFromJson(it)
            if (parsedText.isNotBlank()) {
                onResult(parsedText)
            }
        }
    }

    override fun onPartialResult(hypothesis: String?) {}

    override fun onError(exception: Exception?) {
        onError(exception?.localizedMessage ?: "Unknown speech recognition error")
    }

    override fun onTimeout() {
        stopListening()
    }

    private fun extractTextFromJson(jsonStr: String): String {
        return try {
            val jsonObj = JSONObject(jsonStr)
            jsonObj.optString("text", "")
        } catch (e: Exception) {
            ""
        }
    }

    fun destroy() {
        stopListening()
        voskModel?.close()
    }
}