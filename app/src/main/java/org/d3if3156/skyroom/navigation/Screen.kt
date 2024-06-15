package org.d3if3156.skyroom.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("mainscreen")
    data object Login : Screen("loginscreen")
}