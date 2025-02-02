package com.example.recipeorganizer.models.api

import com.example.recipeorganizer.models.dataprovider.AiRequest
import com.example.recipeorganizer.models.model.GemModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

interface GeminiApi {

    @GET("/models/gemini-1.5-flash:generateContent")
    suspend fun getResponse(
        @Path("key") api: String,
        @Body request: AiRequest
    ) : Response<GemModel>
}