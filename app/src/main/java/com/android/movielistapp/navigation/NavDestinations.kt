package com.android.movielistapp.navigation

/**
 * Defines constants for navigation routes and arguments used within the application.
 * This object serves as a single source of truth for navigation paths,
 * helping to avoid typos and making route management easier.
 */
object AppDestinations {
    const val MOVIE_LIST_ROUTE = "movieList"
    const val MOVIE_DETAIL_ROUTE = "movieDetail"
    const val MOVIE_ID_ARG = "movieId"
    const val MOVIE_DETAIL_WITH_ARG_ROUTE = "$MOVIE_DETAIL_ROUTE/{$MOVIE_ID_ARG}"
}