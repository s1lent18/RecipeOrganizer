package com.example.recipeorganizer.models.model

data class Good(
    val amount: String,
    val indented: Boolean,
    val percentOfDailyNeeds: Double,
    val title: String
)