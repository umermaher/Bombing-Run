package com.umermahar.bombingrun

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umermahar.bombingrun.initial.InitialScreen
import com.umermahar.bombingrun.main.MainScreen
import com.umermahar.bombingrun.utils.Screen

@Composable
fun Navigation() {
//    val rootNavController = rememberNavController()
//
//    NavHost(
//        navController = rootNavController,
//        startDestination = Screen.InitialScreen.route
//    ) {
//
//        composable(route = Screen.InitialScreen.route) {
//            InitialScreen(
//                navigate = { route ->
//                    rootNavController.navigate(route)
//                }
//            )
//        }
//
//        composable(route = Screen.MainScreen.route) {
//            MainScreen()
//        }
//    }

    MainScreen()

}