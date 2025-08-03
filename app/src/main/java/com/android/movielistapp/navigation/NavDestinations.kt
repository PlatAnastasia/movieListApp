package com.android.movielistapp.navigation

object AppDestinations {
    const val MOVIE_LIST_ROUTE = "movieList"
    const val MOVIE_DETAIL_ROUTE = "movieDetail"
    const val MOVIE_ID_ARG = "movieId"
    const val MOVIE_DETAIL_WITH_ARG_ROUTE = "$MOVIE_DETAIL_ROUTE/{$MOVIE_ID_ARG}"
}