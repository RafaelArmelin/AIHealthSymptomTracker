package com.rafaelarmelin.aihealthsymptomtracker.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// ── Request ────────────────────────────────────────────────────────────────────

data class GroqRequest(
    val model: String = "llama-3.1-8b-instant",
    val messages: List<Message>,
    val max_tokens: Int = 500
) {
    data class Message(val role: String, val content: String)
}

// ── Response ───────────────────────────────────────────────────────────────────

data class GroqResponse(val choices: List<Choice>?) {
    data class Choice(val message: GroqRequest.Message?)
}

// ── Retrofit interface ─────────────────────────────────────────────────────────

interface GroqApiService {
    @POST("openai/v1/chat/completions")
    suspend fun generateContent(
        @Header("Authorization") authorization: String,
        @Body request: GroqRequest
    ): GroqResponse
}