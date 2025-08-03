package com.android.movielistapp.ui.movielist.mvi

import com.android.movielistapp.data.Movie
/**
 * Represents the various states of the Movie List screen.
 */
data class MovieListState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentQuery: String = "",
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val canLoadMore: Boolean = false,
    val isSearchActive: Boolean = false,
    val isRefreshing: Boolean = false,
) {
    companion object {
        val Initial: MovieListState = MovieListState()
    }
}
