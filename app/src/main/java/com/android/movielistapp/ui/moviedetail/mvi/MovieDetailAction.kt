package com.android.movielistapp.ui.moviedetail.mvi

sealed class MovieDetailAction {
    data class LoadDetails(val movieId: Int) : MovieDetailAction()
    data object RetryLoadDetails : MovieDetailAction()
    data object NavigateBack : MovieDetailAction()
}