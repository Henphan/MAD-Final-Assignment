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
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.IconButton
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
fun UserPreferenceScreen(
    onBack: () -> Unit,
    viewModel: UserPreferenceViewModel
){
    // remembers the state of the button
    var sliderPosition by remember { mutableStateOf(0f) }

    val scope = rememberCoroutineScope()

    // loads the previous isDarkMode value
    val isDarkMode by viewModel.isDarkMode
        .collectAsStateWithLifecycle(initialValue = false)

    // sets the theme to the previously set theme
    val currTheme = if(isDarkMode) "Dark" else "Light"

    // unsure
    LaunchedEffect(isDarkMode) {
        sliderPosition = if(isDarkMode) 1f else 0f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Preferences") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                WaypointTitle(Modifier.padding(horizontal = 16.dp))
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
                    onValueChange = {sliderPosition = it}, // when slider is being moved --> follows the mouse
                    onValueChangeFinished = { // when slider is let go --> snaps to one side
                        val target =
                            if(sliderPosition < 0.5f) 0f
                            else 1f
                        viewModel.onThemeChange(target == 1f)
                        scope.launch {
                            animate( // button animation stuff
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