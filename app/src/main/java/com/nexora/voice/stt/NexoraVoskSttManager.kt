package com.nexora.voice.stt

import android.content.Context
import android.util.Log
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.File
import java.util.zip.ZipInputStream

/**
 * Vosk offline speech recognition.
 * Model zip asset theke runtime-e unzip kore load kora hoy (Vosk-er jonno folder lagbe).
 */
class NexoraVoskSttManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
) {

    private var model: Model? = null
    private var speechService: SpeechService? = null
    @Volatile private var isListening = false

    fun initializeModel() {
        Thread {
            try {
                val dir = unpackModel(MODEL_ASSET)
                model = Model(dir.absolutePath)
                Log.d(TAG, "Vosk model ready: ${dir.name}")
            } catch (e: Exception) {
                Log.e(TAG, "Model load failed", e)
                this.onError("Speech model load hoyni: ${e.message}")
            }
        }.start()
    }

    fun startListening() {
        val loaded = model
        if (loaded == null) {
            onError("Speech model ekhono ready hoyni, ektu opekkha koro.")
            return
        }
        if (isListening) return

        try {
            val recognizer = Recognizer(loaded, SAMPLE_RATE)
            val service = SpeechService(recognizer, SAMPLE_RATE)
            speechService = service
            isListening = true
            service.startListening(listener)
        } catch (e: Exception) {
            isListening = false
            onError("Shuru korte parlam na: ${e.message}")
        }
    }

    fun stopListening() {
        speechService?.stop()
        isListening = false
    }

    fun destroy() {
        speechService?.shutdown()
        speechService = null
        model?.close()
        model = null
        isListening = false
    }

    private val listener = object : RecognitionListener {
        override fun onPartialResult(hypothesis: String?) = Unit

        override fun onResult(hypothesis: String?) {
            val text = parseText(hypothesis)
            if (text.isNotBlank()) {
                stopListening()
                this@NexoraVoskSttManager.onResult(text)
            }
        }

        override fun onFinalResult(hypothesis: String?) {
            val text = parseText(hypothesis)
            stopListening()
            if (text.isNotBlank()) this@NexoraVoskSttManager.onResult(text)
        }

        override fun onError(exception: Exception?) {
            isListening = false
            this@NexoraVoskSttManager.onError("Recognition error: ${exception?.message}")
        }

        override fun onTimeout() {
            stopListening()
            this@NexoraVoskSttManager.onError("Kichhu shuni ni, abar bolo.")
        }
    }

    private fun parseText(hypothesis: String?): String {
        if (hypothesis.isNullOrBlank()) return ""
        return try {
            JSONObject(hypothesis).optString("text").trim()
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * assets/models/<name>.zip -> filesDir/<name>/ e unzip kore.
     * Zip-er vitore top-level folder thakle sheta-i return hobe.
     */
    private fun unpackModel(assetPath: String): File {
        val rootName = assetPath.substringAfterLast('/').removeSuffix(".zip")
        val target = File(context.filesDir, rootName)
        if (File(target, "am").isDirectory) return target

        context.assets.open(assetPath).use { raw ->
            ZipInputStream(raw).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    val out = File(context.filesDir, entry.name)
                    if (entry.isDirectory) {
                        out.mkdirs()
                    } else {
                        out.parentFile?.mkdirs()
                        out.outputStream().use { zip.copyTo(it) }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        }
        return target
    }

    companion object {
        private const val TAG = "NexoraVoskSttManager"
        private const val MODEL_ASSET = "models/vosk-model-small-en-in-0.4.zip"
        private const val SAMPLE_RATE = 16000f
    }
}
