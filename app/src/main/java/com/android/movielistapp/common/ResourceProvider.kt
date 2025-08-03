package com.android.movielistapp.common


/**
 * Interface for providing resources, primarily strings, to components
 * that don't have direct access to Android [android.content.Context].
 * Useful for ViewModels, Repositories, etc.
 */
interface ResourceProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg formatArgs: Any): String
}