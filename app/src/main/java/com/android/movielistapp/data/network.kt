package com.android.movielistapp.data

import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {

    /**
     * Searches for movies based on a query string.
     *
     * @param apiKey Your TMDB API key.
     * @param query The search query string.
     * @param page The page number of results to retrieve.
     * @return A Retrofit [Response] containing a [MovieSearchResponse].
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): Response<MovieSearchResponse>

    /**
     * Retrieves detailed information for a specific movie by its ID.
     *
     * @param movieId The ID of the movie to retrieve details for.
     * @param apiKey Your TMDB API key.
     * @return A Retrofit [Response] containing the [Movie] details.
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Response<Movie>
}