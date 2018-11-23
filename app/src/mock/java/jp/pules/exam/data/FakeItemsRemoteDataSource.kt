package jp.pules.exam.data

import jp.pules.exam.data.source.ItemsDataSource
import jp.pules.exam.items.domain.model.Item

class FakeItemsRemoteDataSource private constructor() : ItemsDataSource {
    override fun getItems(callback: ItemsDataSource.LoadItemsCallback) {
        callback.onItemsLoaded(ITEMS_SERVICE_DATA.values.toList())
    }

    override fun getItem(itemId: String, callback: ItemsDataSource.GetItemCallback) {
        val item = ITEMS_SERVICE_DATA[itemId]
        if (item != null) {
            callback.onItemLoaded(item)
        }
    }

    override fun saveItem(item: Item) {
    }

    override fun refreshItems() {
        // {@link ItemsRepository}で実行する
    }

    override fun deleteAllItems() {
    }

    companion object {

        private var INSTANCE: FakeItemsRemoteDataSource? = null

        private var ITEMS_SERVICE_DATA : MutableMap<String, Item> = linkedMapOf()

        @JvmStatic
        fun getInstance() : FakeItemsRemoteDataSource {
            if (INSTANCE == null) {
                INSTANCE = FakeItemsRemoteDataSource()
            }
            return INSTANCE!!
        }
    }
}