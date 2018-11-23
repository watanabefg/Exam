package jp.pules.exam.data.source.local

import android.support.annotation.VisibleForTesting
import jp.pules.exam.items.domain.model.Item
import jp.pules.exam.data.source.ItemsDataSource
import jp.pules.exam.utils.AppExecutors

// DB
class ItemsLocalDataSource private constructor(
        val appExecutors: AppExecutors,
        val itemsDao: ItemsDao
) : ItemsDataSource {
    override fun getItems(callback: ItemsDataSource.LoadItemsCallback) {
        appExecutors.diskIO.execute {
            val items = itemsDao.getItems()
            appExecutors.mainThread.execute {
                if (items.isEmpty()) {
                    // テーブル内が空のとき
                    callback.onDataNotAvailable()
                } else {
                    callback.onItemsLoaded(items)
                }
            }
        }
    }

    override fun getItem(itemId: String, callback: ItemsDataSource.GetItemCallback) {
        appExecutors.diskIO.execute {
            val item = itemsDao.getItemById(itemId)

            appExecutors.mainThread.execute {
                if (item != null) {
                    callback.onItemLoaded(item)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun saveItem(item: Item) {
        appExecutors.diskIO.execute { itemsDao.insertItem(item) }
    }

    override fun refreshItems() {
        // {@link ItemsRepository}
    }

    override fun deleteAllItems() {
        appExecutors.diskIO.execute { itemsDao.deleteItems() }
    }

    companion object {
        private var INSTANCE: ItemsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, itemsDao: ItemsDao): ItemsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(ItemsLocalDataSource::javaClass) {
                    INSTANCE = ItemsLocalDataSource(appExecutors, itemsDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}