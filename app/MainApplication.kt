package com.nexora

import android.app.Application
import com.nexora.core.state.TaskStateManager

class MainApplication : Application() {

    lateinit var taskStateManager: TaskStateManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        taskStateManager = TaskStateManager()
    }

    companion object {
        lateinit var instance: MainApplication
            private set
    }
}
