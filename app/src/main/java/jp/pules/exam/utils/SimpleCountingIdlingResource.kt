package jp.pules.exam.utils

import android.support.test.espresso.IdlingResource.ResourceCallback
import android.support.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicInteger


/**
 * An simple counter implementation of [IdlingResource] that determines idleness by
 * maintaining an internal counter. When the counter is 0 - it is considered to be idle, when it is
 * non-zero it is not idle. This is very similar to the way a [java.util.concurrent.Semaphore]
 * behaves.
 *
 *
 * This class can then be used to wrap up operations that while in progress should block tests from
 * accessing the UI.
 */
class SimpleCountingIdlingResource
/**
 * Creates a SimpleCountingIdlingResource
 *
 * @param resourceName the resource name this resource should report to Espresso.
 */
(resourceName: String) : IdlingResource {

    private val resourceName: String = checkNotNull(resourceName)

    private val counter = AtomicInteger(0)

    // written from main thread, read from any thread.
    @Volatile
    private var resourceCallback: ResourceCallback? = null

    override fun getName(): String {
        return resourceName
    }

    override fun isIdleNow(): Boolean {
        return counter.get() == 0
    }

    override fun registerIdleTransitionCallback(resourceCallback: ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     */
    fun increment() {
        counter.getAndIncrement()
    }

    /**
     * Decrements the count of in-flight transactions to the resource being monitored.
     *
     * If this operation results in the counter falling below 0 - an exception is raised.
     *
     * @throws IllegalStateException if the counter is below 0.
     */
    fun decrement() {
        val counterVal = counter.decrementAndGet()
        if (counterVal == 0) {
            // we've gone from non-zero to zero. That means we're idle now! Tell espresso.
            if (resourceCallback != null) {
                resourceCallback!!.onTransitionToIdle()
            }
        }

        if (counterVal < 0) {
            throw IllegalArgumentException("Counter has been corrupted!")
        }
    }
}