package com.example.recipeorganizer.models

import com.example.recipeorganizer.models.api.GeminiApi
import com.example.recipeorganizer.models.api.GetRecipeFullInfoApi
import com.example.recipeorganizer.models.api.GetRecipeIngredientsApi
import com.example.recipeorganizer.models.api.GetRecipeNutrientsApi
import com.example.recipeorganizer.models.api.RecipeDisplayApi
import com.example.recipeorganizer.models.api.SearchApi
import com.example.recipeorganizer.viewmodel.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FoodApiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiApiRetrofit

@Module
@InstallIn(SingletonComponent::class)
object RetrofitInstance {

    private const val BASE_URL_FOOD = "https://api.spoonacular.com/"
    private const val BASE_URL_GEMINI = "https://generativelanguage.googleapis.com/v1beta/"

    @FoodApiRetrofit
    @Provides
    @Singleton
    fun getInstanceFoodApi() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_FOOD)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @GeminiApiRetrofit
    @Provides
    @Singleton
    fun provideGeminiApiRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_GEMINI)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDisplayRecipesApi(@FoodApiRetrofit retrofit: Retrofit) : RecipeDisplayApi {
        return retrofit.create(RecipeDisplayApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiApi(@GeminiApiRetrofit retrofit: Retrofit) : GeminiApi {
        return retrofit.create(GeminiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSearchRecipesApi(@FoodApiRetrofit retrofit: Retrofit) : SearchApi {
        return retrofit.create(SearchApi::class.java)
    }

    @Provides
    @Singleton
    fun provideIngredientsApi(@FoodApiRetrofit retrofit: Retrofit) : GetRecipeIngredientsApi {
        return retrofit.create(GetRecipeIngredientsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRecipeFullInfoApi(@FoodApiRetrofit retrofit: Retrofit) : GetRecipeFullInfoApi {
        return retrofit.create(GetRecipeFullInfoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNutrientsApi(@FoodApiRetrofit retrofit: Retrofit) : GetRecipeNutrientsApi {
        return retrofit.create(GetRecipeNutrientsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }

    @Provides
    @Singleton
    fun provideRepository(): Repository {
        return Repository()
    }
}