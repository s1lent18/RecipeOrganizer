package com.example.recipeorganizer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.recipeorganizer.ui.theme.Chewy
import com.example.recipeorganizer.ui.theme.RecipeOrganizerTheme
import com.example.recipeorganizer.ui.theme.sec
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeOrganizerTheme {
                SplashScreen {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    @Composable
    private fun SplashScreen(onSplashFinished: () -> Unit) {

        var isSplashComplete by remember { mutableStateOf(false) }

        val  text = "TasteBuds"
        var displayedText by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            for (i in 1..text.length) {
                displayedText = text.substring(0, i)
                delay(100)
            }

            delay(500)
            isSplashComplete = true

            onSplashFinished()
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayedText,
                fontSize = 45.sp,
                color = sec,
                fontFamily = Chewy,
                fontWeight = FontWeight.Bold
            )
        }
    }
}