package com.example.recipeorganizer.models.api

import com.example.recipeorganizer.models.dataprovider.AiRequest
import com.example.recipeorganizer.models.model.GemModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApi {

    @POST("models/gemini-1.5-flash:generateContent")
    suspend fun getResponse(
        @Query("key") api: String,
        @Body request: AiRequest
    ) : Response<GemModel>
}