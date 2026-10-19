package com.yesmin.ai.data.repository

import com.yesmin.ai.data.db.AppDatabase
import com.yesmin.ai.data.db.ChatMessageEntity
import com.yesmin.ai.data.db.MemoryEntity
import com.yesmin.ai.data.prefs.PreferencesManager
import com.yesmin.ai.service.gemini.GeminiApiClient
import com.yesmin.ai.service.gemini.PersonalityPromptBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class YesminRepository(
    private val db: AppDatabase,
    private val prefs: PreferencesManager,
    private val geminiClient: GeminiApiClient
) {
    val allMessages: Flow<List<ChatMessageEntity>> = db.chatMessageDao().getAllMessages()
    val allMemories: Flow<List<MemoryEntity>> = db.memoryDao().getAllMemories()

    suspend fun saveUserMessage(text: String) = withContext(Dispatchers.IO) {
        db.chatMessageDao().insert(ChatMessageEntity(sender = "USER", text = text))
    }

    suspend fun saveAiMessage(text: String) = withContext(Dispatchers.IO) {
        db.chatMessageDao().insert(ChatMessageEntity(sender = "YESMIN", text = text))
    }

    suspend fun clearChat() = withContext(Dispatchers.IO) {
        db.chatMessageDao().clearChat()
    }

    suspend fun addMemory(category: String, title: String, content: String) = withContext(Dispatchers.IO) {
        db.memoryDao().insertMemory(MemoryEntity(category = category, title = title, content = content))
    }

    suspend fun deleteMemory(memory: MemoryEntity) = withContext(Dispatchers.IO) {
        db.memoryDao().deleteMemory(memory)
    }

    suspend fun clearMemories() = withContext(Dispatchers.IO) {
        db.memoryDao().clearAll()
    }

    suspend fun generateAiResponse(userPrompt: String, imageBase64: String? = null): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = prefs.geminiApiKey
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception("Please configure your Gemini API Key in Settings first."))
        }

        // Fetch recent memories to ground the companion personality
        val recentMemories = db.memoryDao().getRecentMemories(15)
        val systemInstruction = PersonalityPromptBuilder.buildSystemInstruction(
            mode = prefs.personalityMode,
            userName = prefs.userName,
            memories = recentMemories
        )

        geminiClient.generateContent(
            apiKey = apiKey,
            model = prefs.selectedModel,
            systemPrompt = systemInstruction,
            userMessage = userPrompt,
            imageBase64 = imageBase64
        )
    }
}
