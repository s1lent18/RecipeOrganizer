package com.example.recipeorganizer.viewmodel

import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeorganizer.R
import com.example.recipeorganizer.models.api.RecipeDisplayApi
import com.example.recipeorganizer.models.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisplayRecipesViewModel @Inject constructor(
    private val getdisplayrecipesapi : RecipeDisplayApi,
    @ApplicationContext private val context: Context
) : ViewModel() {

    init {
        getInitialRecipes(type = "breakfast")
    }

    private val _homerecipes = MutableStateFlow<List<Result>>(emptyList())
    val homerecipes : StateFlow<List<Result>> = _homerecipes

    fun getInitialRecipes(
        apiKey: String = context.getString(R.string.FoodApiKey),
        type: String,
        offset: Int = 0,
        number: Int = 10
    ) {
        viewModelScope.launch {
            try {
                val response = getdisplayrecipesapi.getRecipes(
                    apiKey = apiKey,
                    type = type,
                    offset = offset,
                    number = number
                )

                if(response.isSuccessful) {
                    response.body()?.let { responses ->
                        _homerecipes.value = responses.results
                    }
                    Log.d("API Response", "Failed: ${response.body()}")
                }
                else {
                    Log.e("API Response", "Failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API Error", "Exception: ${e.message}")
            }
        }
    }
}