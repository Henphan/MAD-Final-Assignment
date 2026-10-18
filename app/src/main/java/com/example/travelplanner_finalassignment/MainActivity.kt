package com.example.travelplanner_finalassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelplanner_finalassignment.model.AppDatabase
import com.example.travelplanner_finalassignment.model.AppRepository
import com.example.travelplanner_finalassignment.model.PreferencesRepository
import com.example.travelplanner_finalassignment.view.AppNavHost
import com.example.travelplanner_finalassignment.ui.theme.TravelPlannerFinalAssignmentTheme
import com.example.travelplanner_finalassignment.viewmodel.UserPreferenceViewModel
import com.example.travelplanner_finalassignment.viewmodel.UserPreferenceViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = PreferencesRepository(applicationContext)
        val appRepository = AppRepository(AppDatabase.get(applicationContext))

        enableEdgeToEdge()
        setContent {
            val factory = remember(repository) {
                UserPreferenceViewModelFactory(repository)
            }

            val viewModel: UserPreferenceViewModel = viewModel(factory = factory)

            val isDarkMode by viewModel.isDarkMode
                .collectAsStateWithLifecycle(initialValue = false)

            TravelPlannerFinalAssignmentTheme(darkTheme = isDarkMode) {
                AppNavHost(appRepository, viewModel)
            }
        }
    }
}
