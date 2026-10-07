package com.nexora.tools

sealed class ToolResult {
    data class Success(val message: String) : ToolResult()
    data class Failure(val message: String) : ToolResult()
}
