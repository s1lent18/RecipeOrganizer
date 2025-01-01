package com.example.recipeorganizer.models.api

import com.example.recipeorganizer.models.model.IngredientsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GetRecipeIngredientsApi {

    @GET("recipes/{id}/ingredientWidget.json")
    suspend fun getIngredients(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String,
    ) : Response<IngredientsModel>
}