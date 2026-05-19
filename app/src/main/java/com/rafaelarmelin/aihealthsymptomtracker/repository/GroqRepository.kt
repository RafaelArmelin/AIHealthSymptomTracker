package com.rafaelarmelin.aihealthsymptomtracker.repository

import com.rafaelarmelin.aihealthsymptomtracker.BuildConfig
import com.rafaelarmelin.aihealthsymptomtracker.network.GroqApiService
import com.rafaelarmelin.aihealthsymptomtracker.network.GroqRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repository responsible for all communication with the Groq API.
 * Uses Llama 3 via Groq's OpenAI-compatible endpoint.
 */
class GroqRepository {

    private val api: GroqApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApiService::class.java)
    }

    /**
     * Sends a symptom summary to Groq (Llama 3) and returns an informational insight.
     */
    suspend fun getSymptomInsight(symptomName: String, severity: Int, notes: String): String {
        val prompt = buildString {
            append("A user has logged the following health symptom in a personal tracker app: ")
            append("'$symptomName' with a severity rating of $severity out of 5. ")
            if (notes.isNotBlank()) append("Additional notes: '$notes'. ")
            append("Provide a brief, informative, non-diagnostic overview in 2–3 sentences, addressing the user directly using 'you' and 'your'. ")
            append("Suggest whether you should monitor it at home or seek medical advice, speaking directly to the user. ")
            append("End with: 'Note: This is not a medical diagnosis. Please consult a qualified healthcare professional for any medical concerns.'")
        }

        val request = GroqRequest(
            messages = listOf(GroqRequest.Message(role = "user", content = prompt))
        )

        val response = api.generateContent(
            authorization = "Bearer ${BuildConfig.GROQ_API_KEY}",
            request = request
        )

        return response.choices
            ?.firstOrNull()
            ?.message
            ?.content
            ?: "No insight available. Please check your internet connection and try again."
    }
}