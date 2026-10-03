package com.example.travelplanner_finalassignment.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.travelplanner_finalassignment.ui.theme.TravelPlannerFinalAssignmentTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPreferencesScreen(){
    var sliderPosition by remember { mutableStateOf(0f) }

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
            Column() {
                Slider(
                    value = sliderPosition,
                    onValueChange = {sliderPosition = it},
                    onValueChangeFinished = {
                        sliderPosition =
                            if(sliderPosition < 0.5f)
                                0f
                            else
                                1f
                                    },
                    valueRange = 0f .. 1f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp)
                )
            }
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