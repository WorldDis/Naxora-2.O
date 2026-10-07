package com.nexora.tools

data class ToolResult(
    val isSuccess: Boolean,
    val outputData: Map<String, Any?>? = null,
    val errorMessage: String? = null
) {
    companion object {
        fun success(data: Map<String, Any?>? = null) = ToolResult(isSuccess = true, outputData = data)
        fun failure(reason: String) = ToolResult(isSuccess = false, errorMessage = reason)
    }
}
