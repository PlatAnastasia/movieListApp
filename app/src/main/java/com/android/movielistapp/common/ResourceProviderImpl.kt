package com.android.movielistapp.common


import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
/**
 * Default implementation of [ResourceProvider] that uses the application context
 * to retrieve string resources.
 *
 * @param context The application context, injected by Hilt.
 */
@Singleton
class ResourceProviderImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : ResourceProvider {

    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}

