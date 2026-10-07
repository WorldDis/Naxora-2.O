package com.nexora.tools.system

import android.content.Context
import com.nexora.tools.NexoraTool
import com.nexora.tools.RiskTier
import com.nexora.tools.ToolResult

class OpenAppTool(private val context: Context) : NexoraTool {

    override val name: String = "open_app"
    override val description: String = "Launches an installed application using its package name."
    override val riskLevel: RiskTier = RiskTier.LOW

    override suspend fun execute(parameters: Map<String, Any?>): ToolResult {
        val packageName = parameters["packageName"] as? String
            ?: return ToolResult.Failure("Missing parameter: packageName")

        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                ToolResult.Success("Successfully launched application: $packageName")
            } else {
                ToolResult.Failure("Application with package name '$packageName' not found.")
            }
        } catch (e: Exception) {
            ToolResult.Failure("Failed to open app: ${e.localizedMessage}")
        }
    }
}