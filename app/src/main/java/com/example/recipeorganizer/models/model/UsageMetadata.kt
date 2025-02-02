package com.example.recipeorganizer.models.model

data class UsageMetadata(
    val candidatesTokenCount: Int,
    val candidatesTokensDetails: List<CandidatesTokensDetail>,
    val promptTokenCount: Int,
    val promptTokensDetails: List<PromptTokensDetail>,
    val totalTokenCount: Int
)