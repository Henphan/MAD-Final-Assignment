package com.example.travelplanner_finalassignment.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.travelplanner_finalassignment.ui.theme.TravelPlannerFinalAssignmentTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPreferencesScreen(){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Preferences") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Travl Plannr"
                )
            }
        }
    ) {
        innerPadding -> Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text("Hello world")
        }
    }
}

@Composable
@Preview
fun UserPrefScreenPreview(){
    TravelPlannerFinalAssignmentTheme {
        UserPreferencesScreen()
    }
}