package com.example.travelplanner_finalassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelplanner_finalassignment.model.PreferencesRepository
import com.example.travelplanner_finalassignment.ui.theme.TravelPlannerFinalAssignmentTheme
import com.example.travelplanner_finalassignment.view.UserPreferenceScreen
import com.example.travelplanner_finalassignment.viewmodel.UserPreferenceViewModel
import com.example.travelplanner_finalassignment.viewmodel.UserPreferenceViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialising the repository
        val repository = PreferencesRepository(applicationContext)

        enableEdgeToEdge()
        setContent {
            //Initialising the factory, which is what creates the ViewModel
            val factory = remember(repository){
                UserPreferenceViewModelFactory(repository)
            }

            val viewModel: UserPreferenceViewModel =  viewModel(
                factory = factory // allows Android to reuse the existing viewModel where possible
            )

            val isDarkMode by viewModel.isDarkMode
                .collectAsStateWithLifecycle(initialValue = false) // gets current state of theme

            TravelPlannerFinalAssignmentTheme (
                darkTheme = isDarkMode // uses the system dark theme
            ){
                UserPreferenceScreen(viewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TravelPlannerFinalAssignmentTheme {
        Greeting("Android")
    }
}