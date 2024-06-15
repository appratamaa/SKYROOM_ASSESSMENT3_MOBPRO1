package org.d3if3156.skyroom.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.d3if3156.skyroom.network.UserDataStore
import org.d3if3156.skyroom.ui.screen.LoginScreen
import org.d3if3156.skyroom.ui.screen.MainScreen

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController(), userDataStore: UserDataStore) {
    val isLoggedIn by userDataStore.isLoggedInFlow.collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        composable(route = Screen.Home.route) {
            MainScreen(navController)
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navController, userDataStore)
        }
    }
}