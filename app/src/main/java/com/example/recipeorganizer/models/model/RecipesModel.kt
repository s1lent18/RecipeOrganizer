package com.example.recipeorganizer.models.model

data class RecipesModel(
    val number: Int,
    val offset: Int,
    val results: List<Result>,
    val totalResults: Int
)