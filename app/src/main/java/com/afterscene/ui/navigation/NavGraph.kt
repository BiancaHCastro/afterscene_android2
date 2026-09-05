package com.afterscene.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.afterscene.ui.screens.DetailScreen
import com.afterscene.ui.screens.HomeScreen
import com.afterscene.ui.screens.MoodScreen
import com.afterscene.viewmodel.MediaViewModel
import com.afterscene.viewmodel.MoodViewModel

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail/{mediaId}"
    const val MOOD = "mood"
    
    fun detailRoute(mediaId: Long) = "detail/$mediaId"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MediaViewModel,
    moodViewModel: MoodViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToDetail = { mediaId ->
                    navController.navigate(Routes.detailRoute(mediaId))
                },
                onNavigateToMood = {
                    navController.navigate(Routes.MOOD)
                }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("mediaId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: -1L
            DetailScreen(
                viewModel = viewModel,
                mediaId = mediaId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.MOOD) {
            MoodScreen(
                viewModel = moodViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

