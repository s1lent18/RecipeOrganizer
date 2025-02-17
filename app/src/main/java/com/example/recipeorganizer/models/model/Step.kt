package com.example.recipeorganizer.models.model

data class Step(
    val equipment: List<Equipment>,
    val ingredients: List<Any>,
    val number: Int,
    val step: String
)