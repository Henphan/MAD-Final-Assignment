package com.example.travelplanner_finalassignment.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.travelplanner_finalassignment.model.AppRepository
import com.example.travelplanner_finalassignment.model.HomePreferencesRepository
import com.example.travelplanner_finalassignment.viewmodel.HomeViewModel
import com.example.travelplanner_finalassignment.viewmodel.HomeViewModelFactory
import com.example.travelplanner_finalassignment.viewmodel.MapViewModel
import com.example.travelplanner_finalassignment.viewmodel.MapViewModelFactory
import com.example.travelplanner_finalassignment.viewmodel.TripViewModel
import com.example.travelplanner_finalassignment.viewmodel.TripViewModelFactory
import com.example.travelplanner_finalassignment.viewmodel.UserPreferenceViewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object MapRoute

@Serializable
object SettingsRoute

@Serializable
data class TripRoute(val tripId: Int)

@Composable
fun AppNavHost(
    repository: AppRepository,
    homePreferencesRepository: HomePreferencesRepository,
    preferenceViewModel: UserPreferenceViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            val factory = remember(repository, homePreferencesRepository) {
                HomeViewModelFactory(repository, homePreferencesRepository)
            }

            val viewModel: HomeViewModel = viewModel(factory = factory)

            HomeScreen(
                onOpenTrip = { tripId -> navController.navigate(TripRoute(tripId)) },
                onOpenMap = { navController.navigate(MapRoute) },
                onOpenSettings = { navController.navigate(SettingsRoute) },
                viewModel = viewModel
            )
        }
        composable<TripRoute> { backStackEntry ->
            val tripId = backStackEntry.toRoute<TripRoute>().tripId

            val factory = remember(repository, tripId) {
                TripViewModelFactory(repository, tripId)
            }

            val viewModel: TripViewModel = viewModel(factory = factory)

            TripScreen(
                onBack = { navController.popBackStack() },
                onOpenTrip = { newTripId -> navController.navigate(TripRoute(newTripId)) },
                viewModel = viewModel
            )
        }
        composable<MapRoute> {
            val factory = remember(repository) {
                MapViewModelFactory(repository)
            }

            val viewModel: MapViewModel = viewModel(factory = factory)

            MapScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable<SettingsRoute> {
            UserPreferenceScreen(
                onBack = { navController.popBackStack() },
                viewModel = preferenceViewModel
            )
        }
    }
}
