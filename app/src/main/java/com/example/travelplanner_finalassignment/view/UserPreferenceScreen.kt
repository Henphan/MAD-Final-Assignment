package com.example.travelplanner_finalassignment.view

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.travelplanner_finalassignment.ui.theme.TravelPlannerFinalAssignmentTheme
import com.example.travelplanner_finalassignment.viewmodel.UserPreferenceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPreferenceScreen(viewModel: UserPreferenceViewModel){
    var sliderPosition by remember { mutableStateOf(0f) }

    val scope = rememberCoroutineScope()

    val isDarkMode by viewModel.isDarkMode
        .collectAsStateWithLifecycle(initialValue = false)

    val currTheme = if(isDarkMode) "Dark" else "Light"

    LaunchedEffect(isDarkMode) {
        sliderPosition = if(isDarkMode) 1f else 0f
    }

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
                Spacer(modifier = Modifier.padding(8.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Theme: $currTheme"
                )

                Slider(
                    value = sliderPosition,
                    onValueChange = {sliderPosition = it},
                    onValueChangeFinished = {
                        val target =
                            if(sliderPosition < 0.5f) 0f
                            else 1f
                        viewModel.onThemeChange(target == 1f)
                        scope.launch {
                            animate(
                                initialValue = sliderPosition,
                                targetValue = target,
                                animationSpec = spring()
                            ) {
                                value, _ -> sliderPosition = value
                            }
                        }
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

//@Composable
//@Preview
//fun UserPrefScreenPreview(){
//    TravelPlannerFinalAssignmentTheme {
//        UserPreferenceScreen()
//    }
//}