package com.android.movielistapp.ui.movielist.mvi

import com.android.movielistapp.data.Movie


sealed interface MovieListSideEffect {
    data class LoadMovies(val query: String, val page: Int) : MovieListSideEffect
    data class NavigateToMovieDetails(val movie: Movie) : MovieListSideEffect
    data class ShowSnackbar(val message: String) : MovieListSideEffect
}