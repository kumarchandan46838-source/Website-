package com.yesmin.ai.service.gemini

import com.google.gson.Gson
import com.yesmin.ai.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiApiClient {

    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun testConnection(apiKey: String, model: String = "gemini-1.5-flash"): Result<String> = withContext(Dispatchers.IO) {
        val testPrompt = "Respond with one short sentence confirming you are online and ready."
        generateContent(apiKey, model, "You are Yesmin AI.", testPrompt, null)
    }

    suspend fun generateContent(
        apiKey: String,
        model: String,
        systemPrompt: String,
        userMessage: String,
        imageBase64: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

            val parts = mutableListOf<Part>()
            if (!imageBase64.isNullOrBlank()) {
                parts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = imageBase64)))
            }
            parts.add(Part(text = userMessage))

            val userContent = Content(role = "user", parts = parts)
            val systemContent = if (systemPrompt.isNotBlank()) {
                Content(role = "system", parts = listOf(Part(text = systemPrompt)))
            } else null

            val requestBodyObj = GeminiRequest(
                contents = listOf(userContent),
                systemInstruction = systemContent,
                generationConfig = GenerationConfig(temperature = 0.85f, maxOutputTokens = 2048)
            )

            val jsonBody = gson.toJson(requestBodyObj)
            val request = Request.Builder()
                .url(endpoint)
                .post(jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                val errorMsg = try {
                    val errObj = gson.fromJson(responseBody, GeminiResponse::class.java)
                    errObj.error?.message ?: "HTTP Error ${response.code}: ${response.message}"
                } catch (e: Exception) {
                    "HTTP Error ${response.code}: ${response.message}"
                }
                return@withContext Result.failure(IOException(errorMsg))
            }

            val geminiResponse = gson.fromJson(responseBody, GeminiResponse::class.java)
            val answer = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return@withContext Result.failure(IOException("Empty response received from Gemini AI."))

            Result.success(answer.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
