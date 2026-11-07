package com.example.data.ai

import android.graphics.Bitmap

enum class AiRole(
    val title: String,
    val subtitle: String,
    val modelName: String,
    val systemInstruction: String
) {
    COMPLEX_GENETICS(
        title = "Master Geneticist & Vet",
        subtitle = "Deep Pedigree & Pathology (gemini-4-argon)",
        modelName = "gemini-4-argon",
        systemInstruction = """
            You are the Chief Geneticist and Avian Veterinary Specialist of PIGEONVERSE.
            You specialize in pigeon genetics (color morphs, recessive red, ash-red, blue bar, dilute alleles, Punnett squares),
            racing Homer aerodynamics, bloodline line-breeding vs outcrossing, and avian veterinary diagnostics
            (Paramyxovirus PMV-1, Pigeon Pox, Canker/Trichomoniasis, Coccidiosis, Salmonella).
            Provide authoritative, highly technical, and scientifically precise advice to master fanciers.
        """.trimIndent()
    ),
    LOFT_ADVISOR(
        title = "Loft & Fancier Advisor",
        subtitle = "Loft Setup & Care (gemini-3.8-flash)",
        modelName = "gemini-3.8-flash",
        systemInstruction = """
            You are the Senior Pigeon Master and Loft Manager of PIGEONVERSE.
            You assist pigeon fanciers worldwide with loft ventilation, daily feeding rations (protein vs carbs for racing season),
            young bird training, pairing seasons, ring registrations, and marketplace valuations.
            Maintain an encouraging, knowledgeable, and professional tone.
        """.trimIndent()
    ),
    QUICK_ASSISTANT(
        title = "Quick Loft Assistant",
        subtitle = "Instant Lookup (gemini-3.1-flash-lite)",
        modelName = "gemini-3.1-flash-lite",
        systemInstruction = """
            You are the high-speed Pigeonverse assistant. Provide rapid, concise, bulleted answers
            about pigeon breeds, ring number national prefixes (BELG, DV, NL, IND, RO), conversion formulas,
            and immediate quick guidance for fanciers on the go.
        """.trimIndent()
    )
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val searchSources: List<GroundingCitation> = emptyList(),
    val mapPlaces: List<GroundingCitation> = emptyList()
)

data class GroundingCitation(
    val title: String,
    val uri: String,
    val snippet: String = ""
)

sealed interface ImageStudioUiState {
    data object Idle : ImageStudioUiState
    data class Loading(val progressMessage: String) : ImageStudioUiState
    data class Success(val bitmap: Bitmap, val promptUsed: String) : ImageStudioUiState
    data class Error(val message: String) : ImageStudioUiState
}
