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

    init {

        getGeminiPrediction(request = "Healthy Food Tip")
    }

    fun getGeminiPrediction(request: String) {
        val aiRequest = AiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = request)))
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
                        _response.value = responses
                    }
                    Log.d("GEMINI_API", "Response: ${response.body()}")
                } else {
                    Log.e("GEMINI_API", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GEMINI_API", "Exception: ${e.message}")
            }
        }
    }
}