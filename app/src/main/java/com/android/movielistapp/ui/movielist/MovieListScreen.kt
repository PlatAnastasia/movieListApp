package com.android.movielistapp.ui.movielist

import androidx.compose.ui.input.nestedscroll.nestedScroll

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.movielistapp.R
import com.android.movielistapp.data.Movie
import com.android.movielistapp.ui.movielist.mvi.MovieListAction
import com.android.movielistapp.ui.movielist.mvi.MovieListSideEffect
import kotlinx.coroutines.flow.collectLatest



object MovieSelectionHolder {
    var selectedMovie: Movie? = null
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    navController: NavController,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    var isRefreshing by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullToRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.dispatch(MovieListAction.RefreshList)
        }
    )

    // Listen for loading changes to stop refresh
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is MovieListSideEffect.NavigateToMovieDetails -> {
                    MovieSelectionHolder.selectedMovie = effect.movie
                    navController.navigate("movieDetail/${effect.movie.id}")
                }
                is MovieListSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Movie Search (MVI)") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                SearchBar(
                    query = uiState.currentQuery,
                    onQueryChanged = { query -> viewModel.dispatch(MovieListAction.SearchQueryChanged(query)) },
                    onSearchTriggered = { viewModel.dispatch(MovieListAction.SearchTriggered) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                when {
                    uiState.isLoading && uiState.movies.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.error != null && uiState.movies.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    uiState.movies.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No movies found. Try a different search term.",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(uiState.movies, key = { _, movie -> movie.id }) { index, movie ->
                                MovieListItem(
                                    movie = movie,
                                    onMovieClick = { selectedMovie ->
                                        viewModel.dispatch(MovieListAction.MovieClicked(selectedMovie))
                                    }
                                )

                                if (index == uiState.movies.lastIndex && uiState.canLoadMore && !uiState.isLoadingMore) {
                                    LaunchedEffect(Unit) {
                                        viewModel.dispatch(MovieListAction.LoadMoreMovies)
                                    }
                                }
                            }

                            if (uiState.isLoadingMore) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            if (uiState.error != null && uiState.movies.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Error loading more: ${uiState.error}",
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        label = { Text("Search Movies") },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        trailingIcon = {
            IconButton(onClick = {
                onSearchTriggered()
                keyboardController?.hide()
                focusManager.clearFocus()
            }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        },
        // Optional: Search on keyboard done action
        // keyboardActions = KeyboardActions(onSearch = {
        //     onSearchTriggered()
        //     keyboardController?.hide()
        //     focusManager.clearFocus()
        // }),
        // keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
    // Optional: Request focus when the screen loads if isSearchActive is true in state
    // LaunchedEffect(Unit) {
    //     focusRequester.requestFocus()
    // }
}

@Composable
fun MovieListItem(movie: Movie, onMovieClick: (Movie) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top // Align items to the top for better text flow
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.getFullPosterUrl())
                    .crossfade(true)
                    .error(R.drawable.ic_launcher_background) // Replace with a generic placeholder
                    .placeholder(R.drawable.ic_launcher_background)
                    .build(),
                contentDescription = movie.title,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp) // Maintain aspect ratio for posters
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Rating: %.1f".format(movie.voteAverage),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4, // Show a bit more overview
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp // Improve readability
                )
            }
        }
    }
}
