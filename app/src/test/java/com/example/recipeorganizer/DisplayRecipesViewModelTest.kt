

package com.example.recipeorganizer

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.recipeorganizer.models.api.GetRecipeFullInfoApi
import com.example.recipeorganizer.models.api.GetRecipeIngredientsApi
import com.example.recipeorganizer.models.api.GetRecipeNutrientsApi
import com.example.recipeorganizer.models.api.RecipeDisplayApi
import com.example.recipeorganizer.models.api.SearchApi
import com.example.recipeorganizer.models.model.AutoCompleteModelItem
import com.example.recipeorganizer.models.model.Result
import com.example.recipeorganizer.models.model.RecipesModel
import com.example.recipeorganizer.viewmodel.DisplayRecipesViewModel
import com.example.recipeorganizer.models.model.AutoCompleteModel
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
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
class DisplayRecipesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var searchApi: SearchApi

    @Mock
    private lateinit var recipeDisplayApi: RecipeDisplayApi

    @Mock
    private lateinit var getRecipeIngredientsApi: GetRecipeIngredientsApi

    @Mock
    private lateinit var getRecipeFullInfoApi: GetRecipeFullInfoApi

    @Mock
    private lateinit var getRecipeNutrientsApi: GetRecipeNutrientsApi

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: DisplayRecipesViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        `when`(context.getString(R.string.FoodApiKey)).thenReturn("fake-api-key")

        viewModel = DisplayRecipesViewModel(
            searchApi,
            recipeDisplayApi,
            getRecipeIngredientsApi,
            getRecipeFullInfoApi,
            getRecipeNutrientsApi,
            context
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCurrentMealTime returns correct meal time based on hour`() = runTest {
        val initialCategory = viewModel.timeCategory.first()

        assert(initialCategory in listOf("Breakfast", "Lunch", "Dinner"))
    }

    @Test
    fun `updateCalories updates calories stateflow`() = runTest {
        assertEquals(null, viewModel.calories.first())

        viewModel.updateCalories("2000")

        assertEquals("2000", viewModel.calories.first())
    }

    @Test
    fun `updateCuisines updates cuisines stateflow`() = runTest {
        assertEquals(emptyList<String>(), viewModel.cuisines.first())

        val cuisinesList = listOf("Italian", "Mexican")
        viewModel.updateCuisines(cuisinesList)

        assertEquals(cuisinesList, viewModel.cuisines.first())
    }

    @Test
    fun `getRecipes updates homerecipes stateflow with API response`() = runTest {
        val mockResults = listOf(
            Result(
                id = 1,
                title = "Test Recipe",
                image = "test_image_url",
                imageType = "jpg"
            )
        )
        val mockResponse = RecipesModel(
            results = mockResults,
            offset = 0,
            number = 10,
            totalResults = 1
        )

        val successResponse = Response.success(mockResponse)
        `when`(recipeDisplayApi.getRecipes(
            apiKey = anyString(),
            type = anyString(),
            offset = anyInt(),
            number = anyInt(),
            min = anyInt(),
            max = anyInt(),
            cuisine = anyString()
        )).thenReturn(successResponse)

        viewModel.getRecipes(offset = 0)

        assertEquals(mockResults, viewModel.homerecipes.first())
        assertEquals(1, viewModel.total.first())
    }


    @Test
    fun `getSearchRecipes updates searchrecipes stateflow with API response`() = runTest {
        val mockSearchResultItems = listOf(
            AutoCompleteModelItem(
                id = 1,
                title = "Test Recipe",
                imageType = "jpg"
            )
        )

        val mockSearchResults = AutoCompleteModel().apply {
            addAll(mockSearchResultItems)
        }

        val successResponse = Response.success(mockSearchResults)
        `when`(searchApi.getSearchRecipes(
            apiKey = anyString(),
            query = anyString(),
            number = anyInt()
        )).thenReturn(successResponse)

        viewModel.getSearchRecipes(query = "pasta")

        assertEquals(mockSearchResults, viewModel.searchrecipes.first())
    }

    @Test
    fun `clearRecipes empties homerecipes stateflow`() = runTest {
        val mockResults = listOf(
            Result(
                id = 1,
                title = "Test Recipe",
                image = "test_image_url",
                imageType = "jpg"
            )
        )
        val mockResponse = RecipesModel(
            results = mockResults,
            offset = 0,
            number = 10,
            totalResults = 1
        )

        val successResponse = Response.success(mockResponse)
        `when`(recipeDisplayApi.getRecipes(
            apiKey = anyString(),
            type = anyString(),
            offset = anyInt(),
            number = anyInt(),
            min = anyInt(),
            max = anyInt(),
            cuisine = anyString()
        )).thenReturn(successResponse)

        viewModel.getRecipes(offset = 0)

        assertEquals(mockResults, viewModel.homerecipes.first())

        viewModel.clearRecipes()

        assertEquals(emptyList<Result>(), viewModel.homerecipes.first())
    }
}



