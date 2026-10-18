package com.example.travelplanner_finalassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.travelplanner_finalassignment.ui.AppNavHost
import com.example.travelplanner_finalassignment.ui.theme.TravelPlannerFinalAssignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelPlannerFinalAssignmentTheme {
                AppNavHost()
            }
        }
    }
}
