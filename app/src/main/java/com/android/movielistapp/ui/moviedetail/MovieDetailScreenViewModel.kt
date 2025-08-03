package com.android.movielistapp.ui.moviedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.movielistapp.R
import com.android.movielistapp.common.ResourceProvider
import com.android.movielistapp.data.MovieRepository
import com.android.movielistapp.data.NetworkResult
import com.android.movielistapp.ui.moviedetail.mvi.MovieDetailAction
import com.android.movielistapp.ui.moviedetail.mvi.MovieDetailSideEffect
import com.android.movielistapp.ui.moviedetail.mvi.MovieDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
/**
 * ViewModel for the [MovieDetailScreen].
 * Manages the UI state for movie details, handles actions like loading details,
 * and interacts with the [MovieRepository].
 *
 * @param movieRepository The repository to fetch movie data.
 * @param resourceProvider Utility to access string resources.
 */
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailState.Initial)
    val uiState: StateFlow<MovieDetailState> = _uiState.asStateFlow()

    private val _navigationEffect = MutableSharedFlow<MovieDetailSideEffect.TriggerNavigationBack>()
    val navigationEffect: SharedFlow<MovieDetailSideEffect.TriggerNavigationBack> = _navigationEffect.asSharedFlow()


    fun onAction(action: MovieDetailAction) {
        viewModelScope.launch {
            applyActionToState(action)
        }
    }

    private suspend fun applyActionToState(action: MovieDetailAction) {
        val currentState = _uiState.value
        when (action) {
            is MovieDetailAction.LoadDetails -> {
                if (currentState.isLoading && currentState.movieId == action.movieId) return
                _uiState.value = MovieDetailState(isLoading = true, movieId = action.movieId, error = null, movie = null)
                handleSideEffect(MovieDetailSideEffect.FetchMovieDetailsFromApi(action.movieId))
            }
            MovieDetailAction.RetryLoadDetails -> {
                currentState.movieId?.let {
                    _uiState.value = currentState.copy(isLoading = true, error = null, movie = null)
                    handleSideEffect(MovieDetailSideEffect.FetchMovieDetailsFromApi(it))
                } ?: run {
                    _uiState.value = currentState.copy(error = resourceProvider.getString(R.string.error_cannot_retry_no_id), isLoading = false)
                }
            }
            MovieDetailAction.NavigateBack -> {

                handleSideEffect(MovieDetailSideEffect.TriggerNavigationBack)
            }
        }
    }

    private suspend fun handleSideEffect(effect: MovieDetailSideEffect) {
        when (effect) {
            is MovieDetailSideEffect.FetchMovieDetailsFromApi -> {
                when (val result = movieRepository.getMovieDetails(
                    movieId = effect.movieId
                )) {
                    is NetworkResult.Success -> {
                        val movie = result.data
                        if (movie != null) {
                            _uiState.value = _uiState.value.copy(isLoading = false, movie = movie, error = null)
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = resourceProvider.getString(R.string.error_empty_response_body_details),
                                movie = null
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message ?: resourceProvider.getString(R.string.error_unknown_while_fetching_details),
                            movie = null
                        )
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
            MovieDetailSideEffect.TriggerNavigationBack -> {
                _navigationEffect.emit(MovieDetailSideEffect.TriggerNavigationBack)
            }
        }
    }
}

