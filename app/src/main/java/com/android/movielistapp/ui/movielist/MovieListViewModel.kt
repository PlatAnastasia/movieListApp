package com.android.movielistapp.ui.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.movielistapp.data.MovieRepository
import com.android.movielistapp.data.NetworkResult
import com.android.movielistapp.di.NetworkModule
import com.android.movielistapp.ui.movielist.mvi.MovieListAction
import com.android.movielistapp.ui.movielist.mvi.MovieListSideEffect
import com.android.movielistapp.ui.movielist.mvi.MovieListState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieListState.Initial)
    val uiState: StateFlow<MovieListState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<MovieListSideEffect>()
    val sideEffect: SharedFlow<MovieListSideEffect> = _sideEffect.asSharedFlow()

    fun dispatch(action: MovieListAction) {
        when (action) {
            is MovieListAction.RefreshList -> refreshMovies()
            MovieListAction.LoadMoreMovies -> TODO()
            is MovieListAction.MovieClicked -> TODO()
            is MovieListAction.MovieSearchError -> TODO()
            is MovieListAction.MovieSearchSuccess -> TODO()
            is MovieListAction.SearchQueryChanged -> TODO()
            MovieListAction.SearchTriggered -> TODO()
        }
    }

    private suspend fun reduce(action: MovieListAction) {
        val currentState = _uiState.value

        when (action) {
            is MovieListAction.SearchQueryChanged -> {
                _uiState.value = currentState.copy(currentQuery = action.query, isSearchActive = true)
            }

            is MovieListAction.SearchTriggered -> {
                _uiState.value = currentState.copy(isLoading = true, movies = emptyList(), currentPage = 1, error = null)
                handleSideEffect(MovieListSideEffect.LoadMovies(currentState.currentQuery, 1))
            }

            is MovieListAction.LoadMoreMovies -> {
                if (currentState.canLoadMore && !currentState.isLoadingMore) {
                    _uiState.value = currentState.copy(isLoadingMore = true)
                    handleSideEffect(MovieListSideEffect.LoadMovies(currentState.currentQuery, currentState.currentPage + 1))
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
                _uiState.value = currentState.copy(isLoading = true, currentPage = 1, error = null)
                 _uiState.value = currentState.copy(isLoading = true, movies = emptyList(), currentPage = 1, error = null)
                handleSideEffect(MovieListSideEffect.LoadMovies(currentState.currentQuery, 1))
            }
        }
    }
    private fun refreshMovies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            val currentQuery = _uiState.value.currentQuery

            val result = movieRepository.searchMovies("YOUR_API_KEY", currentQuery, page = 1)
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            movies = result.data?.results ?: emptyList(),
                            isRefreshing = false,
                            isLoading = false,
                            canLoadMore = (result.data?.page ?: 1) < (result.data?.totalPages ?: 1),
                            error = null
                        )
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            error = result.message ?: "Unknown error"
                        )
                    }
                }

                else -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
            }
        }
    }
    private suspend fun handleSideEffect(effect: MovieListSideEffect) {
        when (effect) {
            is MovieListSideEffect.LoadMovies -> {
                when (val result = movieRepository.searchMovies(NetworkModule.API_KEY, effect.query, effect.page)) {
                    is NetworkResult.Success -> {
                        val data = result.data
                        if (data != null) {
                            dispatch(MovieListAction.MovieSearchSuccess(data.results, data.page, data.totalPages))
                        } else {
                            dispatch(MovieListAction.MovieSearchError("Empty response from API"))
                        }
                    }
                    is NetworkResult.Error -> {
                        dispatch(MovieListAction.MovieSearchError(result.message ?: "Unknown API error"))
                    }
                    is NetworkResult.Loading -> { /* Handled by isLoading/isLoadingMore flags */ }
                }
            }
            is MovieListSideEffect.NavigateToMovieDetails -> {
                // This effect is typically observed by the UI to perform navigation.
                // The ViewModel itself doesn't navigate; it signals the intent.
                // If _sideEffect is already emitted in reduce, this might be redundant here
                // unless you have a separate flow for effects processed internally vs those sent to UI.
                // For clarity, navigation effect is often emitted directly from reduce when MovieClicked occurs.
            }
            is MovieListSideEffect.ShowSnackbar -> {
                // Similarly, this would be emitted for the UI to observe and display.
                _sideEffect.emit(effect) // Make sure to emit if this is a UI-bound side effect
            }
        }
    }
}
