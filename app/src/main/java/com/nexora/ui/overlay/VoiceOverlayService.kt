package com.nexora.ui.overlay

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import com.nexora.MainApplication
import com.nexora.voice.VoiceEngine

class VoiceOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: FrameLayout? = null
    private var voiceEngine: VoiceEngine? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        voiceEngine = VoiceEngine(this, MainApplication.instance.taskStateManager)
        showOverlay()
    }

    private fun showOverlay() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = 100
        }

        val micButton = Button(this).apply {
            text = "Bolo"
            contentDescription = "Voice command shuru koro"
            setOnClickListener { voiceEngine?.startListening() }
        }

        overlayView = FrameLayout(this).apply { addView(micButton) }
        windowManager?.addView(overlayView, params)
    }

    override fun onDestroy() {
        overlayView?.let { windowManager?.removeView(it) }
        overlayView = null
        voiceEngine?.destroy()
        voiceEngine = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
