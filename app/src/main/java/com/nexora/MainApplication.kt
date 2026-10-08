package com.nexora

import android.app.Application
import com.nexora.core.state.TaskStateManager

class MainApplication : Application() {

    val taskStateManager: TaskStateManager by lazy { TaskStateManager() }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MainApplication
            private set
    }
}
