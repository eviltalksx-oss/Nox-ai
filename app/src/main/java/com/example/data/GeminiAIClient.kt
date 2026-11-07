package com.example.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.GenerativeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import java.util.UUID

class GeminiAIClient(private val customApiKey: String = "") {

    private val model: GenerativeModel by lazy {
        // Using Gemini 3.8 Flash via Firebase AI Logic
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    suspend fun generateResponse(prompt: String, systemInstruction: String = ""): String {
        return withTimeout(60_000L) {
            try {
                val fullPrompt = if (systemInstruction.isNotBlank()) {
                    "$systemInstruction\n\nUser: $prompt"
                } else {
                    prompt
                }
                val response = model.generateContent(fullPrompt)
                response.text ?: "NOX returned an empty signal."
            } catch (e: Exception) {
                Log.e("GeminiAIClient", "Generation failed", e)
                "Error communicating with NOX core: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    fun generateStreamResponse(prompt: String, systemInstruction: String = ""): Flow<String> = flow {
        val fullPrompt = if (systemInstruction.isNotBlank()) {
            "$systemInstruction\n\nUser: $prompt"
        } else {
            prompt
        }
        try {
            withTimeout(60_000L) {
                model.generateContentStream(fullPrompt).collect { chunk ->
                    emit(chunk.text.orEmpty())
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiAIClient", "Stream generation failed", e)
            emit("\n[Signal interrupted: ${e.localizedMessage}]")
        }
    }
}
