package com.example.recipeorganizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.recipeorganizer.ui.theme.RecipeOrganizerTheme
import com.example.recipeorganizer.viewmodel.navigation.NavGraph
import com.example.recipeorganizer.viewmodel.navigation.Screens
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination = Screens.Landing.route

        enableEdgeToEdge()
        setContent {
            RecipeOrganizerTheme {
                navController = rememberNavController()
                NavGraph(startDestination = startDestination)
            }
        }
    }
}