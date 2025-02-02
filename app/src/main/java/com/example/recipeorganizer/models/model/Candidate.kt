package com.example.recipeorganizer.models.model

data class Candidate(
    val avgLogprobs: Double,
    val content: Content,
    val finishReason: String
)