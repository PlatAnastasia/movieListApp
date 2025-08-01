package com.android.movielistapp.data

import javax.inject.Inject
import javax.inject.Singleton

sealed class NetworkResult<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
    class Loading<T> : NetworkResult<T>()
}

interface MovieRepository {
    suspend fun searchMovies(
        apiKey: String,
        query: String,
        page: Int
    ): NetworkResult<MovieSearchResponse>
}

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val tmdbApiService: TmdbApiService
) : MovieRepository {

    override suspend fun searchMovies(
        apiKey: String,
        query: String,
        page: Int
    ): NetworkResult<MovieSearchResponse> {
        return try {
            val response = tmdbApiService.searchMovies(apiKey = apiKey, query = query, page = page)
            if (response.isSuccessful) {
                val responseBody = response.body() // Store in a variable
                if (responseBody != null) {
                    NetworkResult.Success(responseBody)
                } else {
                    NetworkResult.Error("Empty response body from successful call")
                }
            } else {
                NetworkResult.Error("API Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network Error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}