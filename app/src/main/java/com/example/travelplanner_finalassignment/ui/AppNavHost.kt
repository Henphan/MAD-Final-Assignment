package com.example.travelplanner_finalassignment.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object MapRoute

@Serializable
data class TripRoute(val tripId: Long)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
                onOpenTrip = { tripId -> navController.navigate(TripRoute(tripId)) },
                onOpenMap = { navController.navigate(MapRoute) }
            )
        }
        composable<TripRoute> {
            TripScreen(
                onBack = { navController.popBackStack() },
                onOpenTrip = { tripId -> navController.navigate(TripRoute(tripId)) }
            )
        }
        composable<MapRoute> {
            MapScreen(onBack = { navController.popBackStack() })
        }
    }
}
