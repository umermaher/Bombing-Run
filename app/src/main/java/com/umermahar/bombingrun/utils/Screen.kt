package com.umermahar.bombingrun.utils


sealed class Screen(val route:String) {
    data object InitialScreen : Screen("initial_screen")
    data object MainScreen: Screen("main_screen")
}