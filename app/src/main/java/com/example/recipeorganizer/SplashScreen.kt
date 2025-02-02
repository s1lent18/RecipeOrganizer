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
import kotlin.math.max

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
        val centerIndex = text.length / 2
        val center = text[centerIndex].toString()
        val first = text.substring(0, centerIndex)
        val last = text.substring(centerIndex + 1)

        var displayedText by remember { mutableStateOf(center) }

        LaunchedEffect(Unit) {
            for (i in 0 until max(first.length, last.length)) {
                delay(300)

                val leftChar = if (i < first.length) first[first.length - 1 - i] else null
                val rightChar = if (i < last.length) last[i] else null

                displayedText = buildString {
                    if (leftChar != null) append(leftChar)
                    append(displayedText)
                    if (rightChar != null) append(rightChar)
                }
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