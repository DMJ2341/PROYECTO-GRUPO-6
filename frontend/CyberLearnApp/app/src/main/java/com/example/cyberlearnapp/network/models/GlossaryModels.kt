package com.example.cyberlearnapp.network.models

import kotlinx.serialization.Serializable

@Serializable
data class GlossaryResponse(
    val success: Boolean,
    val terms: List<GlossaryTerm>
)

@Serializable
data class GlossaryTerm(
    val id: Int,
    val term: String,
    val definition: String,
    val category: String? = null,
    val example: String? = null
)