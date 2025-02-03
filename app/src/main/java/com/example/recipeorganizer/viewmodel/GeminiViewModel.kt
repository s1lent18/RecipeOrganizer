package com.example.recipeorganizer.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeorganizer.R
import com.example.recipeorganizer.models.api.GeminiApi
import com.example.recipeorganizer.models.dataprovider.AiRequest
import com.example.recipeorganizer.models.dataprovider.Content
import com.example.recipeorganizer.models.dataprovider.Part
import com.example.recipeorganizer.models.model.GemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeminiViewModel @Inject constructor(
    private val geminiapi: GeminiApi,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _response = MutableStateFlow<GemModel?>(null)
    val response : StateFlow<GemModel?> = _response

    private val _mealplan = MutableStateFlow<GemModel?>(null)
    val mealplan : StateFlow<GemModel?> = _mealplan

    private val _breakfast = MutableStateFlow<List<String>>(emptyList())
    val breakfast: StateFlow<List<String>> = _breakfast

    private val _lunch = MutableStateFlow<List<String>>(emptyList())
    val lunch: StateFlow<List<String>> = _lunch

    private val _dinner = MutableStateFlow<List<String>>(emptyList())
    val dinner: StateFlow<List<String>> = _dinner

    private val _snacks = MutableStateFlow<List<String>>(emptyList())
    val snacks: StateFlow<List<String>> = _snacks

    init {
        getGeminiPrediction()
    }

    private fun getGeminiPrediction() {
        val aiRequest = AiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = "Healthy Food Tip")))
            )
        )
        viewModelScope.launch {
            try {
                val response = geminiapi.getResponse(
                    api = context.getString(R.string.GeminiApiKey),
                    request = aiRequest
                )
                if (response.isSuccessful) {
                    response.body()?.let { responses ->
                        responses.candidates[0].content.parts[0].text.replace("*", "")
                        _response.value = responses
                    }
                    Log.d("GEMINI_API", "Response: ${response.body()}")
                } else {
                    Log.e("GEMINI_API", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("GEMINI_API", "Exception: ${e.message}")
            }
        }
    }

    private fun parseMeals(text: String) {
        var currentMeal: MutableStateFlow<List<String>>? = null

        text.lines().forEach { line ->
            val trimmedLine = line.trim()

            when {
                trimmedLine.equals("Breakfast:", ignoreCase = true) -> currentMeal = _breakfast
                trimmedLine.equals("Lunch:", ignoreCase = true) -> currentMeal = _lunch
                trimmedLine.equals("Dinner:", ignoreCase = true) -> currentMeal = _dinner
                trimmedLine.equals("Snacks:", ignoreCase = true) -> currentMeal = _snacks
                currentMeal != null && trimmedLine.isNotEmpty() ->
                    currentMeal!!.value += trimmedLine
            }
        }
    }

    private fun clean(request: String) {
        val aiRequest = AiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = "Can you just extract the breakfast, lunch, dinner and snacks and discard everything else: $request")))
            )
        )
        viewModelScope.launch {
            try {
                val response = geminiapi.getResponse(
                    api = context.getString(R.string.GeminiApiKey),
                    request = aiRequest
                )
                if (response.isSuccessful) {
                    response.body()?.let { responses ->
                        _mealplan.value = responses
                        _mealplan.value!!.candidates[0].content.parts[0].text = _mealplan.value!!.candidates[0].content.parts[0].text.trimIndent()
                        _mealplan.value!!.candidates[0].content.parts[0].text = _mealplan.value!!.candidates[0].content.parts[0].text.replace("*", "").trim()
                        parseMeals(_mealplan.value!!.candidates[0].content.parts[0].text)
                    }
                    Log.d("GEMINI_API", "Response: ${response.body()}")
                } else {
                    Log.e("GEMINI_API", "Error: ${response.code()}")
                }
            }
            catch (e: Exception) {
                Log.e("GEMINI_API", "Exception: ${e.message}")
            }
        }
    }

    fun getGeminiPrediction1(request: String) {
        val aiRequest = AiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = request)))
            )
        )
        viewModelScope.launch {
            try {
                Log.d("GEMINI_API", "Response: $request")
                val response = geminiapi.getResponse(
                    api = context.getString(R.string.GeminiApiKey),
                    request = aiRequest
                )
                if (response.isSuccessful) {
                    response.body()?.let { mealplan ->
                        clean(request = mealplan.candidates[0].content.parts[0].text)
                    }
                    Log.d("GEMINI_API", "Response: ${response.body()}")
                } else {
                    Log.e("GEMINI_API", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("GEMINI_API", "Exception: ${e.message}")
            }
        }
    }
}