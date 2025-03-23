package com.example.recipeorganizer.models.api

import com.example.recipeorganizer.models.model.InstructionsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GetInstructionsApi {

    @GET("recipes/{id}/analyzedInstructions")
    suspend fun getInstructionsInfo(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String,
    ) : Response<InstructionsModel>
}