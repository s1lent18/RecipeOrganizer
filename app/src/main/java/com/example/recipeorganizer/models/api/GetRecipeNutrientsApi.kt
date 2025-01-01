package com.example.recipeorganizer.models.api

import com.example.recipeorganizer.models.model.IngredientsModel
import com.example.recipeorganizer.models.model.NutrientsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GetRecipeNutrientsApi {

    @GET("recipes/{id}/nutritionWidget.json")
    suspend fun getNutrients(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String,
    ) : Response<NutrientsModel>
}