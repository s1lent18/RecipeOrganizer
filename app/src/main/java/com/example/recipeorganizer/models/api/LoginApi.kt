package com.example.recipeorganizer.models.api

import com.example.recipeorganizer.models.model.LoginModel
import com.example.recipeorganizer.models.requests.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {

    @POST("/Login")
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ) : Response<LoginModel>
}

