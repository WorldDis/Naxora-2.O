package com.nexora.tools

import org.json.JSONArray
import org.json.JSONObject

class ToolRegistry {
    private val tools = mutableMapOf<String, NexoraTool>()

    fun registerTool(tool: NexoraTool) {
        tools[tool.name] = tool
    }

    fun getTool(name: String): NexoraTool? = tools[name]

    fun getAllTools(): List<NexoraTool> = tools.values.toList()

    fun getToolsJsonSchema(): String {
        val array = JSONArray()
        tools.values.forEach { tool ->
            array.put(
                JSONObject()
                    .put("name", tool.name)
                    .put("description", tool.description)
                    .put("riskLevel", tool.riskLevel.name)
            )
        }
        return array.toString()
    }
}
