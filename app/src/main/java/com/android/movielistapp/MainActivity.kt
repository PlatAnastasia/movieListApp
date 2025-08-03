package com.android.movielistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.android.movielistapp.navigation.AppNavigation
import com.android.movielistapp.ui.theme.MovieListAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point of the application.
 * This [ComponentActivity] hosts the Jetpack Compose UI and sets up
 * the navigation graph.
 **/
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
