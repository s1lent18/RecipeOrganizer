package com.example.recipeorganizer

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.recipeorganizer.models.api.GeminiApi
import com.example.recipeorganizer.viewmodel.GeminiViewModel
import com.example.recipeorganizer.models.dataprovider.AiRequest
import com.example.recipeorganizer.models.model.Candidate
import com.example.recipeorganizer.models.model.Content
import com.example.recipeorganizer.models.model.GemModel
import com.example.recipeorganizer.models.model.UsageMetadata
import com.example.recipeorganizer.models.model.Part
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import org.mockito.kotlin.any


@ExperimentalCoroutinesApi
class GeminiViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var geminiApi: GeminiApi

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: GeminiViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        Mockito.`when`(context.getString(R.string.GeminiApiKey)).thenReturn("fake-api-key")

        val mockGemModel = createMockGemModel("Test healthy food tip")
        val successResponse = Response.success(mockGemModel)

        runTest {
            Mockito.`when`(geminiApi.getResponse(anyString(), any<AiRequest>())).thenReturn(successResponse)
        }

        viewModel = GeminiViewModel(geminiApi, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls getGeminiPrediction and updates response StateFlow`() = runTest {
        val responseValue = viewModel.response.first()

        assertEquals("Test healthy food tip", responseValue?.candidates?.get(0)?.content?.parts?.get(0)?.text)
    }


    @Test
    fun `getInstructions updates instructions StateFlow with recipe steps`() = runTest {
        val recipeSteps = "1. Prepare ingredients\n2. Cook the meal\n3. Serve hot"
        val mockInstructionsModel = createMockGemModel(recipeSteps)
        val successResponse = Response.success(mockInstructionsModel)

        Mockito.`when`(geminiApi.getResponse(anyString(), any())).thenReturn(successResponse)

        viewModel.getInstructions("Pasta")

        val instructionsValue = viewModel.instructions.first()
        val extractedText = instructionsValue?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
        assertEquals(recipeSteps, extractedText)
    }

    @Test
    fun `getGeminiPrediction removes asterisks from response text`() = runTest {
        val tipWithAsterisks = "*Eat* your *vegetables*"
        val tipWithoutAsterisks = "Eat your vegetables"

        val mockResponseWithAsterisks = createMockGemModel(tipWithAsterisks)
        val successResponse = Response.success(mockResponseWithAsterisks)

        Mockito.reset(geminiApi)
        Mockito.`when`(geminiApi.getResponse(anyString(), any())).thenReturn(successResponse)

        val newViewModel = GeminiViewModel(geminiApi, context)

        val responseValue = newViewModel.response.first()
        assertEquals(tipWithoutAsterisks, responseValue?.candidates?.get(0)?.content?.parts?.get(0)?.text)
    }

    @Test
    fun `getGeminiPrediction handles API error`() = runTest {
        val errorResponse: Response<GemModel> = Response.error(
            400,
            okhttp3.ResponseBody.create(null, "Bad Request")
        )

        Mockito.reset(geminiApi)
        Mockito.`when`(geminiApi.getResponse(anyString(), any())).thenReturn(errorResponse)

        val newViewModel = GeminiViewModel(geminiApi, context)

        val responseValue = newViewModel.response.first()
        assertEquals(null, responseValue)
    }

    @Test
    fun `getGeminiPrediction handles exception`() = runTest {
        Mockito.reset(geminiApi)
        Mockito.`when`(geminiApi.getResponse(anyString(), any())).thenThrow(RuntimeException("Network error"))

        val newViewModel = GeminiViewModel(geminiApi, context)

        val responseValue = newViewModel.response.first()
        assertEquals(null, responseValue)
    }

    private fun createMockGemModel(text: String): GemModel {
        return GemModel(
            candidates = listOf(
                Candidate(
                    content = Content(
                        parts = listOf(
                            Part(text = text)
                        ),
                        role = "model"
                    ),
                    finishReason = "STOP",
                    avgLogprobs = 0.0
                )
            ),
            modelVersion = "gemini-pro-1.0",
            usageMetadata = UsageMetadata(
                candidatesTokenCount = 0,
                candidatesTokensDetails = emptyList(),
                promptTokenCount = 0,
                promptTokensDetails = emptyList(),
                totalTokenCount = 0
            )
        )
    }
}