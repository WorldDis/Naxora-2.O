package com.nexora

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nexora.core.eventbus.NexoraEvent
import com.nexora.core.eventbus.NexoraEventBus
import com.nexora.core.state.TaskState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Simple UI for debugging core states
        statusTextView = TextView(this).apply {
            textSize = 20f
            text = "NEXORA Agent State: IDLE"
            setPadding(32, 32, 32, 32)
        }
        setContentView(statusTextView)

        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            NexoraEventBus.events.collectLatest { event ->
                when (event) {
                    is NexoraEvent.StateChanged -> {
                        statusTextView.text = "State: ${event.newState}"
                    }
                    is NexoraEvent.SpeakFeedback -> {
                        // Spoken feedback indicator
                    }
                    is NexoraEvent.ErrorOccurred -> {
                        statusTextView.text = "Error: ${event.error}"
                    }
                    else -> {}
                }
            }
        }
    }
}
