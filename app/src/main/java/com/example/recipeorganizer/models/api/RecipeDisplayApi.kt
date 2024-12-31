package com.example.recipeorganizer.models.api

import com.example.recipeorganizer.models.model.RecipesModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeDisplayApi {

    @GET("recipes/complexSearch")
    suspend fun getRecipes(
        @Query("apiKey") apiKey: String,
        @Query("type") type: String,
        @Query("offset") offset: Int,
        @Query("number") number: Int
    ) : Response<RecipesModel>
}