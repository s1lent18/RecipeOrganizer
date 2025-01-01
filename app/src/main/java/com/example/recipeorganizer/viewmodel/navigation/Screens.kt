package com.example.recipeorganizer.viewmodel.navigation

sealed class Screens(val route: String) {
    data object Home: Screens("Home_Screen")
    data object Landing: Screens("Landing_Screen")
    data object Single: Screens("Single_Screen")
}