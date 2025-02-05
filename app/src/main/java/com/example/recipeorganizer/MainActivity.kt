package com.example.recipeorganizer

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
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
import android.Manifest


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionRequestCode = 123
    private lateinit var navController: NavHostController

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {

                // Request permission if not granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    permissionRequestCode
                )
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

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionRequestCode)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
    }
}