package com.android.movielistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.movielistapp.navigation.AppDestinations
import com.android.movielistapp.navigation.AppNavigation
import com.android.movielistapp.ui.moviedetail.MovieDetailScreen
import com.android.movielistapp.ui.movielist.MovieListScreen
import com.android.movielistapp.ui.theme.MovieListAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieListAppTheme {
                AppNavigation()
            }
        }
    }
}

