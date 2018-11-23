package jp.pules.exam

class TestUseCaseScheduler : UseCaseScheduler {
    override fun execute(runnable: Runnable) {
        runnable.run()
    }

    override fun <R : UseCase.ResponseValue> notifyResponse(
            response: R,
            useCaseCallback: UseCase.UseCaseCallback<R>) {
        useCaseCallback.onSuccess(response)
    }

    override fun <R : UseCase.ResponseValue> onError(
            useCaseCallback: UseCase.UseCaseCallback<R>) {
        useCaseCallback.onError()
    }
}