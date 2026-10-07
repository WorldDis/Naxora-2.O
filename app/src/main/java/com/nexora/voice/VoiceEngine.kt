package com.nexora.voice

import android.content.Context
import android.util.Log
import com.nexora.core.eventbus.NexoraEvent
import com.nexora.core.eventbus.NexoraEventBus
import com.nexora.core.state.TaskState
import com.nexora.core.state.TaskStateManager
import com.nexora.voice.stt.NexoraVoskSttManager
import com.nexora.voice.tts.NexoraTtsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VoiceEngine(
    context: Context,
    private val taskStateManager: TaskStateManager,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {

    private val ttsManager = NexoraTtsManager(context) { isReady ->
        if (isReady) {
            Log.d("VoiceEngine", "TTS initialized successfully.")
        }
    }

    private val sttManager = NexoraVoskSttManager(
        context = context,
        onResult = { transcript ->
            handleSpokenTranscript(transcript)
        },
        onError = { error ->
            NexoraEventBus.emit(NexoraEvent.ErrorOccurred("STT Error: $error"))
            taskStateManager.transitionTo(TaskState.FAILED)
        }
    )

    init {
        sttManager.initializeModel()
        observeEvents()
    }

    private fun observeEvents() {
        coroutineScope.launch {
            NexoraEventBus.events.collectLatest { event ->
                when (event) {
                    is NexoraEvent.SpeakFeedback -> {
                        ttsManager.speak(event.text)
                    }
                    is NexoraEvent.StopRequested -> {
                        stopAll()
                    }
                    else -> {}
                }
            }
        }
    }

    fun startListening() {
        taskStateManager.transitionTo(TaskState.LISTENING)
        sttManager.startListening()
    }

    fun stopListening() {
        sttManager.stopListening()
    }

    private fun handleSpokenTranscript(transcript: String) {
        sttManager.stopListening()
        taskStateManager.transitionTo(TaskState.UNDERSTANDING)
        
        // EventBus-এ ব্যবহারকারীর ভয়েস কমান্ড পাঠিয়ে দেয়া হচ্ছে
        NexoraEventBus.emit(NexoraEvent.UserSpokenInput(transcript))
    }

    fun stopAll() {
        sttManager.stopListening()
        ttsManager.stop()
    }

    fun destroy() {
        sttManager.destroy()
        ttsManager.shutdown()
    }
}