package com.example.recipeorganizer.viewmodel.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recipeorganizer.models.dataprovider.AiRequest
import com.example.recipeorganizer.models.dataprovider.Content
import com.example.recipeorganizer.models.dataprovider.Part
import com.example.recipeorganizer.view.Home
import com.example.recipeorganizer.view.Landing
import com.example.recipeorganizer.view.Single
import com.example.recipeorganizer.viewmodel.DisplayRecipesViewModel
import com.example.recipeorganizer.viewmodel.GeminiViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String
) {

    val displayrecipesviewmodel = hiltViewModel<DisplayRecipesViewModel>()
    val geminiviewmodel = hiltViewModel<GeminiViewModel>()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        this.composable(
            route = Screens.Landing.route
        ) { Landing(navController = navController) }

        this.composable(
            route = Screens.Home.route
        ) { Home(
            navController = navController,
            displayrecipesviewmodel = displayrecipesviewmodel,
            onLoadMore = { offset -> displayrecipesviewmodel.getRecipes(offset = offset) },
            loadAnother = { type, clear -> displayrecipesviewmodel.getRecipes(type = type, offset = 0, clear = clear) },
            searchRecipes = { query -> displayrecipesviewmodel.getSearchRecipes(query = query) },
            specificRecipe = {
                id -> displayrecipesviewmodel.getIngredients(id = id)
                displayrecipesviewmodel.getRecipeFullInfo(id = id)
                displayrecipesviewmodel.getNutrients(id = id)
            }
        ) }

        this.composable(
            route = Screens.Single.route
        ) {


            Single(
                navController = navController,
                displayrecipesviewmodel = displayrecipesviewmodel,
                sendRequest = {
                     request -> geminiviewmodel.getGeminiPrediction(request = request)
                }
            )
        }
    }
}