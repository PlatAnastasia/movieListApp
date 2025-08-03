package com.android.movielistapp.ui.moviedetail.mvi

sealed class MovieDetailSideEffect {
    data class FetchMovieDetailsFromApi(val movieId: Int) : MovieDetailSideEffect()
    data object TriggerNavigationBack : MovieDetailSideEffect()
}