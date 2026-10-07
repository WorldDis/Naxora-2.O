package com.nexora.brain.provider

import com.nexora.brain.model.AIPlanResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GeminiAIProvider(private val apiKey: String) : AIProvider {

    override val providerId: String = "GEMINI_FREE_TIER"

    override suspend fun generatePlan(
        systemPrompt: String,
        userQuery: String,
        availableToolsJson: String,
        currentContextState: String
    ): AIPlanResponse = withContext(Dispatchers.IO) {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
        
        val promptText = """
            $systemPrompt
            Available Tools: $availableToolsJson
            Current UI Context: $currentContextState
            User Goal: $userQuery
            
            Respond ONLY in valid JSON format:
            {
              "reasoning": "step description",
              "selectedTool": "tool_name",
              "toolParameters": {},
              "isTaskComplete": false,
              "finalResponseToUser": "speech text"
            }
        """.trimIndent()

        val requestBody = JSONObject().apply {
            put("contents", org.json.JSONArray().put(JSONObject().apply {
                put("parts", org.json.JSONArray().put(JSONObject().apply {
                    put("text", promptText)
                }))
            }))
        }

        val url = URL(endpoint)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true

        conn.outputStream.use { os ->
            os.write(requestBody.toString().toByteArray())
        }

        if (conn.responseCode == 200) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            return@withContext parseGeminiResponse(responseText)
        } else {
            return@withContext AIPlanResponse(
                reasoning = "API Request Failed with code: ${conn.responseCode}",
                selectedTool = null,
                toolParameters = null,
                isTaskComplete = true,
                finalResponseToUser = "Apologies, I encountered an AI service connection error."
            )
        }
    }

    private fun parseGeminiResponse(jsonResponse: String): AIPlanResponse {
        return try {
            val root = JSONObject(jsonResponse)
            val textContent = root.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
                .trim()
                .removeSurrounding("```json", "```")
                .trim()

            val parsed = JSONObject(textContent)
            val paramsMap = mutableMapOf<String, Any?>()
            parsed.optJSONObject("toolParameters")?.let { paramsObj ->
                val keys = paramsObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    paramsMap[key] = paramsObj.get(key)
                }
            }

            AIPlanResponse(
                reasoning = parsed.optString("reasoning", ""),
                selectedTool = parsed.optString("selectedTool", null),
                toolParameters = paramsMap,
                isTaskComplete = parsed.optBoolean("isTaskComplete", false),
                finalResponseToUser = parsed.optString("finalResponseToUser", null)
            )
        } catch (e: Exception) {
            AIPlanResponse(
                reasoning = "Parsing Error: ${e.localizedMessage}",
                selectedTool = null,
                toolParameters = null,
                isTaskComplete = true,
                finalResponseToUser = "I couldn't process the response correctly."
            )
        }
    }
}