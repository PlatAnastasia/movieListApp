package com.android.movielistapp.ui.movielist


import com.android.movielistapp.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.movielistapp.common.ResourceProvider
import com.android.movielistapp.data.MovieRepository
import com.android.movielistapp.data.NetworkResult
import com.android.movielistapp.ui.movielist.mvi.MovieListAction
import com.android.movielistapp.ui.movielist.mvi.MovieListSideEffect
import com.android.movielistapp.ui.movielist.mvi.MovieListState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieListState.Initial)
    val uiState: StateFlow<MovieListState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<MovieListSideEffect>()
    val sideEffect: SharedFlow<MovieListSideEffect> = _sideEffect.asSharedFlow()

    fun onUserAction(action: MovieListAction) {
        viewModelScope.launch {
            applyActionToState(action)
        }
    }

    private suspend fun applyActionToState(action: MovieListAction) {
        val currentState = _uiState.value

        when (action) {
            is MovieListAction.SearchQueryChanged -> {
                _uiState.value = currentState.copy(
                    currentQuery = action.query,
                    isSearchActive = true
                )
            }

            is MovieListAction.SearchTriggered -> {
                _uiState.value = currentState.copy(
                    isLoading = true,
                    movies = emptyList(),
                    currentPage = 1,
                    error = null
                )
                handleSideEffect(MovieListSideEffect.LoadMovies(currentState.currentQuery, 1))
            }

            is MovieListAction.LoadMoreMovies -> {
                if (currentState.canLoadMore && !currentState.isLoadingMore) {
                    _uiState.value = currentState.copy(isLoadingMore = true)
                    handleSideEffect(
                        MovieListSideEffect.LoadMovies(
                            currentState.currentQuery,
                            currentState.currentPage + 1
                        )
                    )
                }
            }

            is MovieListAction.MovieSearchSuccess -> {
                val newMovies = action.movies
                _uiState.value = currentState.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    movies = if (action.page == 1) newMovies else currentState.movies + newMovies,
                    currentPage = action.page,
                    totalPages = action.totalPages,
                    canLoadMore = action.page < action.totalPages && newMovies.isNotEmpty(),
                    error = null
                )
            }

            is MovieListAction.MovieSearchError -> {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = action.errorMessage
                )
            }

            is MovieListAction.MovieClicked -> {
                _sideEffect.emit(MovieListSideEffect.NavigateToMovieDetails(action.movie))
            }

            is MovieListAction.RefreshList -> {
                _uiState.value = currentState.copy(
                    isLoading = true,
                    movies = emptyList(),
                    currentPage = 1,
                    error = null
                )
                handleSideEffect(MovieListSideEffect.LoadMovies(currentState.currentQuery, 1))
            }
        }
    }


    private suspend fun handleSideEffect(effect: MovieListSideEffect) {
        when (effect) {
            is MovieListSideEffect.LoadMovies -> {
                when (val result = movieRepository.searchMovies( effect.query, effect.page)) {
                    is NetworkResult.Success -> {
                        val data = result.data
                        if (data != null) {
                            onUserAction(
                                MovieListAction.MovieSearchSuccess(
                                    movies = data.results,
                                    page = data.page,
                                    totalPages = data.totalPages
                                )
                            )
                        } else {
                            onUserAction(MovieListAction.MovieSearchError(resourceProvider.getString(R.string.error_empty_response_from_api)))
                        }
                    }

                    is NetworkResult.Error -> {
                        val errorMessage = result.message ?: resourceProvider.getString(R.string.error_unknown_api_error)
                        onUserAction(MovieListAction.MovieSearchError(result.message ?: errorMessage))
                    }

                    is NetworkResult.Loading -> {}
                }
            }

            is MovieListSideEffect.NavigateToMovieDetails -> {
                _sideEffect.emit(effect)
            }

            is MovieListSideEffect.ShowSnackbar -> {
                _sideEffect.emit(effect)
            }
        }
    }
}
