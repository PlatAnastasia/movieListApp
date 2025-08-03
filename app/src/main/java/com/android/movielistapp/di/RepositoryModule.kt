package com.android.movielistapp.di

import com.android.movielistapp.data.MovieRepository
import com.android.movielistapp.data.MovieRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module responsible for providing repository dependencies.
 * This module declares how to provide an instance of [MovieRepository]
 * when it's requested as a dependency.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}