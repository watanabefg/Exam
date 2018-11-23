package jp.pules.exam

/**
 * Use cases are the entry points to the domain layer.
 *
 * @param <Q> the request type
 * @param <P> the response type
 */
abstract class UseCase<Q : UseCase.RequestValues, P : UseCase.ResponseValue> {

    var requestValues: Q? = null
    var useCaseCallback: UseCaseCallback<P>? = null

    internal fun run() {
        executeUseCase(requestValues)
    }

    protected abstract fun executeUseCase(requestValues: Q?)

    /**
     * Data passed to a request.
     */
    interface RequestValues

    /**
     * Data received from a request.
     */
    interface ResponseValue

    interface UseCaseCallback<R> {
        fun onSuccess(response: R)
        fun onError()
    }
}