package com.yesmin.ai.data.model

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
    val generationConfig: GenerationConfig? = null
)

data class Content(
    val role: String, // "user" or "model"
    val parts: List<Part>
)

data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

data class InlineData(
    val mimeType: String,
    val data: String // Base64 encoded data
)

data class GenerationConfig(
    val temperature: Float = 0.8f,
    val topK: Int = 40,
    val topP: Float = 0.95f,
    val maxOutputTokens: Int = 2048
)

data class GeminiResponse(
    val candidates: List<Candidate>?,
    val promptFeedback: PromptFeedback?,
    val error: GeminiError?
)

data class Candidate(
    val content: Content?,
    val finishReason: String?
)

data class PromptFeedback(
    val blockReason: String?
)

data class GeminiError(
    val code: Int?,
    val message: String?,
    val status: String?
)
