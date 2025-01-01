package com.example.recipeorganizer.models.api

import com.example.recipeorganizer.models.model.RecipeFullInfoModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GetRecipeFullInfoApi {

    @GET("recipes/{id}/information")
    suspend fun getFullInfo(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String,
    ) : Response<RecipeFullInfoModel>
}