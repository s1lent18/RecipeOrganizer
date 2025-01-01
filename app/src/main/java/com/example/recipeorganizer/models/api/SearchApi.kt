package com.example.recipeorganizer.models.api

import com.example.recipeorganizer.models.model.AutoCompleteModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("recipes/autocomplete")
    suspend fun getSearchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String,
        @Query("number") number: Int
    ) : Response<AutoCompleteModel>
}