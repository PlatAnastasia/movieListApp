package com.android.movielistapp.data

import com.android.movielistapp.R
import com.android.movielistapp.common.ResourceProvider
import com.android.movielistapp.di.NetworkModule
import javax.inject.Inject
import javax.inject.Singleton

sealed class NetworkResult<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
    class Loading<T> : NetworkResult<T>()
}

interface MovieRepository {
    suspend fun searchMovies(
        query: String,
        page: Int
    ): NetworkResult<MovieSearchResponse>
    suspend fun getMovieDetails(
        movieId: Int
    ): NetworkResult<Movie>
}

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val tmdbApiService: TmdbApiService,
    private val resourceProvider: ResourceProvider
) : MovieRepository {

    override suspend fun searchMovies(
        query: String,
        page: Int
    ): NetworkResult<MovieSearchResponse> {
        return try {
            val response = tmdbApiService.searchMovies(apiKey = NetworkModule.API_KEY, query = query, page = page)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    NetworkResult.Success(responseBody)
                } else {
                    NetworkResult.Error(resourceProvider.getString(R.string.error_empty_response_body))
                }
            } else {
                val responseCode = response.code().toString()
                val responseMessage = response.message() ?: resourceProvider.getString(R.string.error_api_generic)

                NetworkResult.Error(
                    resourceProvider.getString(R.string.error_api_generic, responseCode, responseMessage)
                )
            }
        } catch (e: Exception) {
            val errorMessage = e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown)
            NetworkResult.Error(resourceProvider.getString(R.string.error_network_generic, errorMessage))
        }
    }

    override suspend fun getMovieDetails(
        movieId: Int
    ): NetworkResult<Movie> {
        return try {

            val response = tmdbApiService.getMovieDetails(movieId = movieId, apiKey = NetworkModule.API_KEY,)
            if (response.isSuccessful) {
                val movieDetail = response.body()
                if (movieDetail != null) {
                    NetworkResult.Success(movieDetail)
                } else {
                    NetworkResult.Error(resourceProvider.getString(R.string.error_unknown_while_fetching_details))
                }
            } else {
                val responseCode = response.code().toString()
                val responseMessage = response.message() ?: resourceProvider.getString(R.string.error_api_generic)

                NetworkResult.Error(
                    resourceProvider.getString(R.string.error_api_generic, responseCode, responseMessage)
                )
            }
        } catch (e: Exception) {
            val errorMessage = e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown)
            NetworkResult.Error(errorMessage)
        }
    }
}