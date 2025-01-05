package com.example.recipeorganizer.models.api

import com.example.recipeorganizer.models.model.LoginModel
import com.example.recipeorganizer.models.requests.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignupApi {

    @POST("/Signup")
    suspend fun registerUser(
        @Body signuprequest: SignupRequest
    ) : Response<LoginModel>
}