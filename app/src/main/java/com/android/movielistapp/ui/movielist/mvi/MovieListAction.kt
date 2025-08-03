package com.android.movielistapp.ui.movielist.mvi

import com.android.movielistapp.data.Movie

/**
 * Defines all possible user actions that can occur on the Movie List screen.
 */
sealed interface MovieListAction {
    // User-initiated actions
    data class SearchQueryChanged(val query: String) : MovieListAction
    data object SearchTriggered : MovieListAction
    data object LoadMoreMovies : MovieListAction
    data class MovieClicked(val movie: Movie) : MovieListAction
    data object RefreshList : MovieListAction

    data class MovieSearchSuccess(
        val movies: List<Movie>,
        val page: Int,
        val totalPages: Int
    ) : MovieListAction
    data class MovieSearchError(val errorMessage: String) : MovieListAction
}
