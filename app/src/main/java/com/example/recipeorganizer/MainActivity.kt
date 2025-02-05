package com.example.recipeorganizer

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.recipeorganizer.models.dataprovider.breakfastNotifications
import com.example.recipeorganizer.models.dataprovider.dinnerNotifications
import com.example.recipeorganizer.models.dataprovider.lunchNotifications
import com.example.recipeorganizer.ui.theme.RecipeOrganizerTheme
import com.example.recipeorganizer.viewmodel.NotificationViewModel
import com.example.recipeorganizer.viewmodel.navigation.NavGraph
import com.example.recipeorganizer.viewmodel.navigation.Screens
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        val viewModel: NotificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        val breakfastTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val lunchTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val dinnerTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }

        viewModel.scheduleNotification(breakfastTime.timeInMillis,"Good Morning! 🌞", breakfastNotifications.random())
        viewModel.scheduleNotification(lunchTime.timeInMillis, "Lunch Time! 🍔", lunchNotifications.random())
        viewModel.scheduleNotification(dinnerTime.timeInMillis, "Dinner's Ready! 🍕", dinnerNotifications.random())

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