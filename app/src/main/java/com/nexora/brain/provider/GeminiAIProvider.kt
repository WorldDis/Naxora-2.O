package com.nexora.brain.provider

import com.nexora.brain.model.AIPlanResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class GeminiAIProvider(
    private val apiKey: String,
    private val model: String = "gemini-2.0-flash"
) : AIProvider {

    override val providerId: String = "GEMINI_FREE_TIER"

    override suspend fun generatePlan(
        systemPrompt: String,
        userQuery: String,
        availableToolsJson: String,
        currentContextState: String
    ): AIPlanResponse = withContext(Dispatchers.IO) {
        val prompt = """
            $systemPrompt
            Available Tools: $availableToolsJson
            Current UI Context: $currentContextState
            User Goal: $userQuery
            Respond ONLY in valid JSON: {"reasoning":"...","selectedTool":"...","toolParameters":{},"isTaskComplete":false,"finalResponseToUser":"..."}
        """.trimIndent()

        val body = JSONObject().put(
            "contents",
            JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", prompt))))
        ).toString()

        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 30_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("x-goog-api-key", apiKey)
        }

        try {
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code in 200..299) {
                parseGeminiResponse(text)
            } else {
                AIPlanResponse("HTTP $code", null, null, true, "AI service-e somossa hocche.")
            }
        } catch (e: IOException) {
            AIPlanResponse("Network error: ${e.message}", null, null, true, "Internet connection check koro.")
        } finally {
            conn.disconnect()
        }
    }

    private fun parseGeminiResponse(json: String): AIPlanResponse = try {
        val raw = JSONObject(json)
            .getJSONArray("candidates").getJSONObject(0)
            .getJSONObject("content").getJSONArray("parts")
            .getJSONObject(0).getString("text")

        val cleaned = raw.trim()
            .removePrefix("```json").removePrefix("```")
            .removeSuffix("```").trim()

        val parsed = JSONObject(cleaned)
        val params: Map<String, Any?> = parsed.optJSONObject("toolParameters")?.let { o ->
            o.keys().asSequence().associateWith { o.get(it) }
        } ?: emptyMap()

        AIPlanResponse(
            reasoning = parsed.optString("reasoning", ""),
            selectedTool = parsed.optString("selectedTool").takeIf { it.isNotBlank() },
            toolParameters = params,
            isTaskComplete = parsed.optBoolean("isTaskComplete", false),
            finalResponseToUser = parsed.optString("finalResponseToUser").takeIf { it.isNotBlank() }
        )
    } catch (e: Exception) {
        AIPlanResponse("Parse error: ${e.message}", null, null, true, "Uttarta bujhte parlam na.")
    }
}
