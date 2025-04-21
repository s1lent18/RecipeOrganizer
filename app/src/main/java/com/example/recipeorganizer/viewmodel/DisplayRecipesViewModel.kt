package com.example.recipeorganizer.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeorganizer.R
import com.example.recipeorganizer.models.api.GetRecipeFullInfoApi
import com.example.recipeorganizer.models.api.GetRecipeIngredientsApi
import com.example.recipeorganizer.models.api.GetRecipeNutrientsApi
import com.example.recipeorganizer.models.api.RecipeDisplayApi
import com.example.recipeorganizer.models.api.SearchApi
import com.example.recipeorganizer.models.model.AutoCompleteModelItem
import com.example.recipeorganizer.models.model.Ingredient
import com.example.recipeorganizer.models.model.NutrientsModel
import com.example.recipeorganizer.models.model.RecipeFullInfoModel
import com.example.recipeorganizer.models.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DisplayRecipesViewModel @Inject constructor(
    private val getsearchrecipesapi: SearchApi,
    private val getdisplayrecipesapi : RecipeDisplayApi,
    private val getingredientsapi: GetRecipeIngredientsApi,
    private val getrecipefullinfoapi: GetRecipeFullInfoApi,
    private val getrecipenutrientsapi: GetRecipeNutrientsApi,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _timeCategory = MutableStateFlow(getCurrentMealTime())
    val timeCategory: StateFlow<String> = _timeCategory

    private fun getCurrentMealTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..11 -> "Breakfast"
            in 12..16 -> "Lunch"
            in 17..21 -> "Dinner"
            else -> "Dinner"
        }
    }

    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total

    private val _homerecipes = MutableStateFlow<List<Result>>(emptyList())
    val homerecipes : StateFlow<List<Result>> = _homerecipes

    private val _nutrientsinfo = MutableStateFlow<NutrientsModel?>(null)
    val nutrientsinfo : StateFlow<NutrientsModel?> = _nutrientsinfo

    private val _recipefullinfo = MutableStateFlow<RecipeFullInfoModel?>(null)
    val recipefullinfo : StateFlow<RecipeFullInfoModel?> = _recipefullinfo

    private val _ingredientsrecipes = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredientsrecipes : StateFlow<List<Ingredient>> = _ingredientsrecipes

    private val _searchrecipes = MutableStateFlow<List<AutoCompleteModelItem>>(emptyList())
    val searchrecipes : StateFlow<List<AutoCompleteModelItem>> = _searchrecipes

    private val _calories = MutableStateFlow<String?>(null)
    val calories: StateFlow<String?> = _calories

    private val _cuisines = MutableStateFlow<List<String>>(emptyList())
    val cuisines : StateFlow<List<String>> = _cuisines

    fun updateCalories(newCalories: String?) {
        _calories.value = newCalories
    }

    fun updateCuisines(newCuisinces: List<String>) {
        _cuisines.value = newCuisinces
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

                Log.d("type", type)
                Log.d("offset", "$offset")
                Log.d("number", "$number")
                Log.d("min", "${if (timeCategory.value == "Breakfast") (calories.value?.toInt()?.times(0.20))?.toInt() ?: 0 else if (timeCategory.value == "Lunch") (calories.value?.toInt()?.times(0.35))?.toInt() ?: 0 else if (timeCategory.value == "Dinner") (calories.value?.toInt()?.times(0.25))?.toInt() ?: 0 else (calories.value?.toInt()?.times(0.05))?.toInt() ?: 0}")
                Log.d("max", "${if (timeCategory.value == "Breakfast") (calories.value?.toInt()?.times(0.25))?.toInt() ?: 0 else if (timeCategory.value == "Lunch") (calories.value?.toInt()?.times(0.40))?.toInt() ?: 0 else if (timeCategory.value == "Dinner") (calories.value?.toInt()?.times(0.30))?.toInt() ?: 0 else (calories.value?.toInt()?.times(0.15))?.toInt() ?: 0}")
                Log.d("cuisine",
                    cuisines.value.joinToString(", ")
                )

                val response = getdisplayrecipesapi.getRecipes(
                    apiKey = apiKey,
                    type = type,
                    offset = offset,
                    number = number,
                    min = 0,
                    max = if (timeCategory.value == "Breakfast") (calories.value?.toInt()?.times(0.45))?.toInt() ?: 0 else if (timeCategory.value == "Lunch") (calories.value?.toInt()?.times(0.7))?.toInt() ?: 0 else if (timeCategory.value == "Dinner") (calories.value?.toInt()?.times(0.5))?.toInt() ?: 0 else (calories.value?.toInt()?.times(0.8))?.toInt() ?: 0,
                    cuisine = cuisines.value.joinToString(", ")
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

    fun getIngredients(
        apiKey: String = context.getString(R.string.FoodApiKey),
        id: Int
    ) {
        viewModelScope.launch {
            try {
                val response = getingredientsapi.getIngredients(
                    apiKey = apiKey,
                    id = id
                )

                if(response.isSuccessful) {
                    response.body()?.let { responses ->
                        _ingredientsrecipes.value = responses.ingredients
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

    fun clearRecipes() {
        _homerecipes.value = emptyList()
    }

    fun getRecipeFullInfo(
        apiKey: String = context.getString(R.string.FoodApiKey),
        id: Int
    ) {
        viewModelScope.launch {
            try {
                val response = getrecipefullinfoapi.getFullInfo(
                    apiKey = apiKey,
                    id = id
                )

                if(response.isSuccessful) {
                    response.body()?.let { responses ->
                        _recipefullinfo.value = responses
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

    fun getNutrients(
        apiKey: String = context.getString(R.string.FoodApiKey),
        id: Int
    ) {
        viewModelScope.launch {
            try {
                val response = getrecipenutrientsapi.getNutrients(
                    apiKey = apiKey,
                    id = id
                )

                if(response.isSuccessful) {
                    response.body()?.let { responses ->
                        _nutrientsinfo.value = responses
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