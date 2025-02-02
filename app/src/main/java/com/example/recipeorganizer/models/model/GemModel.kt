package com.example.recipeorganizer.models.model

data class GemModel(
    val candidates: List<Candidate>,
    val modelVersion: String,
    val usageMetadata: UsageMetadata
)