package com.example.recipeorganizer.models.requests

data class SignupRequest(
    val email: String,
    val username: String,
    val password: String
)
