package com.android.movielistapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.movielistapp.ui.moviedetail.MovieDetailScreen
import com.android.movielistapp.ui.movielist.MovieListScreen
import com.android.movielistapp.ui.movielist.MovieSelectionHolder
/**
 * Defines the main navigation graph for the application using Jetpack Navigation Compose.
 * This composable sets up the [NavHost] and declares all the navigable screens (destinations)
 * and the navigation actions between them.
 * **/
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.MOVIE_LIST_ROUTE,
        modifier = modifier
    ) {
        composable(AppDestinations.MOVIE_LIST_ROUTE) {
            MovieListScreen(
                onNavigateToMovieDetail = { movieId, movie ->
                    MovieSelectionHolder.selectedMovie = movie
                    navController.navigate("${AppDestinations.MOVIE_DETAIL_ROUTE}/$movieId")
                },
            )
        }
        composable(
            route = AppDestinations.MOVIE_DETAIL_WITH_ARG_ROUTE,
            arguments = listOf(navArgument(AppDestinations.MOVIE_ID_ARG) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt(AppDestinations.MOVIE_ID_ARG)
            MovieDetailScreen(
                movieId = movieId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
