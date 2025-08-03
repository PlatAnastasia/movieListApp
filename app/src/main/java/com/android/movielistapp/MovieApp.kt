package com.android.movielistapp
import android.app.Application
import dagger.hilt.android.HiltAndroidApp


/**
 * Custom [Application] class for the MovieListApp.
 *
 * This class is annotated with [@HiltAndroidApp], which triggers Hilt's code generation,
 * including a base class for the application that supports dependency injection.
 * This setup is necessary for Hilt to work correctly in the application.
 *
 * It's also the entry point for application-level initializations if needed in the future,
 * though for this project, its primary role is to enable Hilt.
 */
@HiltAndroidApp
class MovieApp : Application()