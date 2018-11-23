package jp.pules.exam

import android.content.Context
import android.support.annotation.NonNull
import jp.pules.exam.UseCaseHandler
import jp.pules.exam.data.source.ItemsRepository
import jp.pules.exam.data.source.local.ExamDatabase
import jp.pules.exam.data.source.local.ItemsLocalDataSource
import jp.pules.exam.data.source.remote.ItemsRemoteDataSource
import jp.pules.exam.itemdetail.domain.usecase.GetItem
import jp.pules.exam.items.domain.usecase.GetItems
import jp.pules.exam.utils.AppExecutors

/**
 * Enables injection of mock implementations for
 * [ItemsDataSource] at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
object Injection {

    fun provideItemsRepository(context: Context): ItemsRepository {
        val database = ExamDatabase.getInstance(context)
        return ItemsRepository.getInstance(
                ItemsRemoteDataSource.getInstance(),
                ItemsLocalDataSource.getInstance(AppExecutors(), database.itemsDao())
        )
    }

    fun provideGetItems(context: Context): GetItems {
        return GetItems(provideItemsRepository(context))
    }

    fun provideUseCaseHandler(): UseCaseHandler {
        return UseCaseHandler.getInstance()
    }

    fun provideGetItem(context: Context): GetItem {
        return GetItem(Injection.provideItemsRepository(context))
    }

}