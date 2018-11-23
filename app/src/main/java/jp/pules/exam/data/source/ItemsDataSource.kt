package jp.pules.exam.data.source

import jp.pules.exam.items.domain.model.Item


/**
 * Main entry point for accessing items data.
 *
 *
 * For simplicity, only getItems() and getItem() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new item is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
interface ItemsDataSource {

    interface LoadItemsCallback {

        fun onItemsLoaded(items: List<Item>)

        fun onDataNotAvailable()
    }

    interface GetItemCallback {

        fun onItemLoaded(item: Item)

        fun onDataNotAvailable()
    }

    fun getItems(callback: LoadItemsCallback)

    fun getItem(itemId: String, callback: GetItemCallback)

    fun saveItem(item: Item)

    fun refreshItems()

    fun deleteAllItems()

}