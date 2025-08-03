package com.android.movielistapp.ui.moviedetail.mvi
/**
 * Defines one-time side effects that the MovieDetailViewModel can trigger.
 */
sealed class MovieDetailSideEffect {
    data class FetchMovieDetailsFromApi(val movieId: Int) : MovieDetailSideEffect()
    data object TriggerNavigationBack : MovieDetailSideEffect()
}