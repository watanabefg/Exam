package jp.pules.exam

import android.content.Context
import jp.pules.exam.data.FakeItemsRemoteDataSource
import jp.pules.exam.data.source.ItemsRepository
import jp.pules.exam.data.source.local.ExamDatabase
import jp.pules.exam.data.source.local.ItemsLocalDataSource
import jp.pules.exam.itemdetail.domain.usecase.GetItem
import jp.pules.exam.items.domain.usecase.GetItems
import jp.pules.exam.utils.AppExecutors

object Injection {

    fun provideItemsRepository(context: Context): ItemsRepository {
        val database = ExamDatabase.getInstance(context)
        return ItemsRepository.getInstance(
                FakeItemsRemoteDataSource.getInstance(),
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