package com.android.movielistapp.ui.movielist.mvi

import com.android.movielistapp.data.Movie


/**
 * Defines one-time side effects that the ViewModel can trigger,
 * such as navigation or showing a toast.
 */
sealed interface MovieListSideEffect {
    data class LoadMovies(val query: String, val page: Int) : MovieListSideEffect
    data class NavigateToMovieDetails(val movie: Movie) : MovieListSideEffect
    data class ShowSnackbar(val message: String) : MovieListSideEffect
}