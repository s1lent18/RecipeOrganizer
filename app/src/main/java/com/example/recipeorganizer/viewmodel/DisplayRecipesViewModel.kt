package com.example.recipeorganizer.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeorganizer.R
import com.example.recipeorganizer.models.api.RecipeDisplayApi
import com.example.recipeorganizer.models.api.SearchApi
import com.example.recipeorganizer.models.model.AutoCompleteModelItem
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
    private val getsearchrecipesapi: SearchApi,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _homerecipes = MutableStateFlow<List<Result>>(emptyList())
    val homerecipes : StateFlow<List<Result>> = _homerecipes

    private val _searchrecipes = MutableStateFlow<List<AutoCompleteModelItem>>(emptyList())
    val searchrecipes : StateFlow<List<AutoCompleteModelItem>> = _searchrecipes

    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total

    init {
        getRecipes(offset = 0)
    }

    fun getRecipes(
        apiKey: String = context.getString(R.string.FoodApiKey),
        type: String = "breakfast",
        offset: Int,
        number: Int = 10,
        clear: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                if (clear) {
                    _homerecipes.value = emptyList()
                }

                val response = getdisplayrecipesapi.getRecipes(
                    apiKey = apiKey,
                    type = type,
                    offset = offset,
                    number = number
                )

                if(response.isSuccessful) {
                    response.body()?.let { responses ->
                        _homerecipes.value += responses.results
                        _total.value = responses.totalResults
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

    fun getSearchRecipes(
        apiKey: String = context.getString(R.string.FoodApiKey),
        query: String,
        number: Int = 10,
    ) {
        viewModelScope.launch {
            try {
                val response = getsearchrecipesapi.getSearchRecipes(
                    apiKey = apiKey,
                    query = query,
                    number = number
                )

                if(response.isSuccessful) {
                    response.body()?.let { responses ->
                        _searchrecipes.value = responses
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