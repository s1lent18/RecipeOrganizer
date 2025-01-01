package com.example.recipeorganizer.viewmodel.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recipeorganizer.view.Home
import com.example.recipeorganizer.view.Landing
import com.example.recipeorganizer.viewmodel.DisplayRecipesViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String
) {

    val displayrecipesviewmodel = hiltViewModel<DisplayRecipesViewModel>()

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
            displayrecipesviewmodel = displayrecipesviewmodel,
            onLoadMore = { offset -> displayrecipesviewmodel.getRecipes(offset = offset) },
            loadAnother = { type, clear -> displayrecipesviewmodel.getRecipes(type = type, offset = 0, clear = clear) },
            searchRecipes = { query -> displayrecipesviewmodel.getSearchRecipes(query = query) }
        ) }
    }
}