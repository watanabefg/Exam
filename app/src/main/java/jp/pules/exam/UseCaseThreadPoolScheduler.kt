package jp.pules.exam

import android.os.Handler
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class UseCaseThreadPoolScheduler : UseCaseScheduler {

    private val handler = Handler()

    internal var threadPoolExecutor: ThreadPoolExecutor

    init {
        threadPoolExecutor = ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, TIMEOUT,
                TimeUnit.SECONDS, ArrayBlockingQueue<Runnable>(POOL_SIZE))
    }

    override fun execute(runnable: Runnable) {
        threadPoolExecutor.execute(runnable)
    }

    override fun <V : UseCase.ResponseValue> notifyResponse(
            response: V,
            useCaseCallback: UseCase.UseCaseCallback<V>) {
        handler.post { useCaseCallback.onSuccess(response) }
    }

    override fun <V : UseCase.ResponseValue> onError(
            useCaseCallback: UseCase.UseCaseCallback<V>) {
        handler.post { useCaseCallback.onError() }
    }

    companion object {
        const val POOL_SIZE = 2
        const val MAX_POOL_SIZE = 4
        const val TIMEOUT = 30L
    }

}