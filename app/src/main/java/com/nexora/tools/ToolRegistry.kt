package com.nexora.tools

class ToolRegistry {
    private val tools = mutableMapOf<String, NexoraTool>()

    fun registerTool(tool: NexoraTool) {
        tools[tool.name] = tool
    }

    fun getTool(name: String): NexoraTool? = tools[name]

    fun getAllTools(): List<NexoraTool> = tools.values.toList()

    fun getToolsJsonSchema(): String {
        val schemas = tools.values.joinToString(",") { tool ->
            """{"name": "${tool.name}", "description": "${tool.description}", "riskLevel": "${tool.riskLevel}"}"""
        }
        return "[$schemas]"
    }
}