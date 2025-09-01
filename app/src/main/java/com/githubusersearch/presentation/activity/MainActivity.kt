package com.githubusersearch.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.githubusersearch.presentation.screens.GithubUserSearchScreen
import com.githubusersearch.presentation.screens.UserProfileScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "search"
            ) {
                //Search screen
                composable("search") {
                    GithubUserSearchScreen(navController = navController)
                }

                //Profile screen with username arg
                composable(
                    route = "profile/{username}",
                    arguments = listOf(navArgument("username") { type = NavType.StringType })
                ) { backStackEntry ->
                    val username = backStackEntry.arguments?.getString("username") ?: ""
                    UserProfileScreen(username = username)
                }
            }
        }
    }
}
