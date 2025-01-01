package com.example.recipeorganizer.models.model

data class NutrientsModel(
    val bad: List<Bad>,
    val caloricBreakdown: CaloricBreakdown,
    val calories: String,
    val carbs: String,
    val expires: Long,
    val fat: String,
    val flavonoids: List<Flavonoid>,
    val good: List<Good>,
    val ingredients: List<IngredientX>,
    val isStale: Boolean,
    val nutrients: List<NutrientX>,
    val properties: List<Property>,
    val protein: String,
    val weightPerServing: WeightPerServing
)