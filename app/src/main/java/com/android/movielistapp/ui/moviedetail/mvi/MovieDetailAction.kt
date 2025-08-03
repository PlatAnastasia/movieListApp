package com.android.movielistapp.ui.moviedetail.mvi

/**
 * Defines all possible user actions that can occur on the Movie Detail screen.
 */
sealed class MovieDetailAction {
    data class LoadDetails(val movieId: Int) : MovieDetailAction()
    data object RetryLoadDetails : MovieDetailAction()
    data object NavigateBack : MovieDetailAction()
}