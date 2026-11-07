package com.example.data.ai

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

class PigeonverseAiRepository {

    private val TAG = "PigeonverseAiRepo"

    // Multi-turn chat models configured lazily per role
    fun createChatSession(role: AiRole): Chat {
        val model = Firebase.ai.generativeModel(
            modelName = role.modelName,
            systemInstruction = content { text(role.systemInstruction) }
        )
        return model.startChat()
    }

    fun streamChatMessage(chat: Chat, userMessage: String): Flow<String> = flow {
        withTimeout(60_000L) {
            chat.sendMessageStream(userMessage).collect { chunk ->
                chunk.text?.let { emit(it) }
            }
        }
    }

    suspend fun sendChatMessage(chat: Chat, userMessage: String): String {
        return withTimeout(60_000L) {
            val response = chat.sendMessage(userMessage)
            response.text ?: "No response from model."
        }
    }

    // Google Search Grounded query (gemini-3.8-flash with Tool.googleSearch())
    suspend fun queryWithSearchGrounding(query: String): Pair<String, List<GroundingCitation>> {
        return withTimeout(60_000L) {
            val model = Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                systemInstruction = content {
                    text("You are the live Pigeonverse Search Intelligence assistant. Retrieve real-time verified facts about pigeon racing, tournaments, health notices, and breed standards.")
                },
                tools = listOf(Tool.googleSearch())
            )

            val response = model.generateContent(query)
            val text = response.text ?: "No response received."

            val citations = mutableListOf<GroundingCitation>()
            response.candidates.firstOrNull()?.groundingMetadata?.groundingChunks?.forEach { chunk ->
                chunk.web?.let { web ->
                    citations.add(
                        GroundingCitation(
                            title = web.title ?: "Web Source",
                            uri = web.uri ?: ""
                        )
                    )
                }
            }

            Pair(text, citations)
        }
    }

    // Google Maps Grounded query (gemini-3.8-flash with Tool.googleMaps())
    suspend fun queryWithMapsGrounding(query: String): Pair<String, List<GroundingCitation>> {
        return withTimeout(60_000L) {
            val model = Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                systemInstruction = content {
                    text("You are the Pigeonverse Geospatial Guide. Provide accurate place recommendations, addresses, and directions for pigeon lofts, avian clinics, pet supply stores, and liberation race points using Google Maps data.")
                },
                tools = listOf(Tool.googleMaps())
            )

            val response = model.generateContent(query)
            val text = response.text ?: "No response received."

            val places = mutableListOf<GroundingCitation>()
            response.candidates.firstOrNull()?.groundingMetadata?.groundingChunks?.forEach { chunk ->
                chunk.web?.let { web ->
                    places.add(
                        GroundingCitation(
                            title = web.title ?: "Maps Location",
                            uri = web.uri ?: ""
                        )
                    )
                }
            }

            Pair(text, places)
        }
    }

    // Image Creation with gemini-nano-banana-2.1
    suspend fun generatePigeonImage(prompt: String): Bitmap? {
        return withTimeout(60_000L) {
            val config = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
            val model = Firebase.ai.generativeModel(
                modelName = "gemini-nano-banana-2.1",
                generationConfig = config
            )
            val response = model.generateContent(prompt)
            val imagePart = response.candidates.firstOrNull()
                ?.content?.parts?.filterIsInstance<ImagePart>()
                ?.firstOrNull()

            imagePart?.image
        }
    }

    // Image Editing with gemini-nano-banana-2.1
    suspend fun editPigeonImage(inputBitmap: Bitmap, editInstruction: String): Bitmap? {
        return withTimeout(60_000L) {
            val config = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
            val model = Firebase.ai.generativeModel(
                modelName = "gemini-nano-banana-2.1",
                generationConfig = config
            )
            val response = model.generateContent(
                content {
                    image(inputBitmap)
                    text(editInstruction)
                }
            )
            val imagePart = response.candidates.firstOrNull()
                ?.content?.parts?.filterIsInstance<ImagePart>()
                ?.firstOrNull()

            imagePart?.image
        }
    }
}
