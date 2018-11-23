package jp.pules.exam.utils

import android.support.test.espresso.IdlingResource


/**
 * Contains a static reference to [IdlingResource], only available in the 'mock' build type.
 */
object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    private val DEFAULT_INSTANCE = SimpleCountingIdlingResource(RESOURCE)

    val idlingResource: IdlingResource
        get() = DEFAULT_INSTANCE

    fun increment() {
        DEFAULT_INSTANCE.increment()
    }

    fun decrement() {
        DEFAULT_INSTANCE.decrement()
    }
}