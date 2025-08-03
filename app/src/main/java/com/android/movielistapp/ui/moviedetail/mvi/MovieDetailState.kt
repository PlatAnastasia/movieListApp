package com.android.movielistapp.ui.moviedetail.mvi

import com.android.movielistapp.data.Movie
/**
 * Represents the various states of the Movie Detail screen.
 */
data class MovieDetailState(
    val movie: Movie? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val movieId: Int? = null
){
    companion object {
        val Initial = MovieDetailState()
    }
}