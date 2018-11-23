package jp.pules.exam

import jp.pules.exam.utils.EspressoIdlingResource

/**
 * Runs [UseCase]s using a [UseCaseScheduler].
 */
class UseCaseHandler(private val useCaseScheduler: UseCaseScheduler) {

    fun <T : UseCase.RequestValues, R : UseCase.ResponseValue> execute(
            useCase: UseCase<T, R>, values: T, callback: UseCase.UseCaseCallback<R>) {
        useCase.requestValues = values
        useCase.useCaseCallback = UiCallbackWrapper(callback, this)

        // The network request might be handled in a different thread so make sure
        // Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment() // App is busy until further notice

        useCaseScheduler.execute(Runnable {
            useCase.run()
            // This callback may be called twice, once for the cache and once for loading
            // the data from the server API, so we check before decrementing, otherwise
            // it throws "Counter has been corrupted!" exception.
            if (!EspressoIdlingResource.idlingResource.isIdleNow) {
                EspressoIdlingResource.decrement() // Set app as idle.
            }
        })
    }

    fun <V : UseCase.ResponseValue> notifyResponse(response: V,
                                                   useCaseCallback: UseCase.UseCaseCallback<V>) {
        useCaseScheduler.notifyResponse(response, useCaseCallback)
    }

    private fun <V : UseCase.ResponseValue> notifyError(
            useCaseCallback: UseCase.UseCaseCallback<V>) {
        useCaseScheduler.onError(useCaseCallback)
    }

    private class UiCallbackWrapper<V : UseCase.ResponseValue>(
            private val callback: UseCase.UseCaseCallback<V>,
            private val useCaseHandler: UseCaseHandler) : UseCase.UseCaseCallback<V> {

        override fun onSuccess(response: V) {
            useCaseHandler.notifyResponse(response, callback)
        }

        override fun onError() {
            useCaseHandler.notifyError(callback)
        }
    }

    companion object {

        private var INSTANCE: UseCaseHandler? = null

        fun getInstance() : UseCaseHandler {
            if (INSTANCE == null) {
                INSTANCE = UseCaseHandler(UseCaseThreadPoolScheduler())
            }
            return INSTANCE!!
        }
    }
}