package jp.pules.exam.data.source

import jp.pules.exam.items.domain.model.Item

class ItemsRepository(
        val itemsRemoteDataSource: ItemsDataSource,
        val itemsLocalDataSource: ItemsDataSource
) : ItemsDataSource {

    companion object {
        private var instance: ItemsRepository? = null

        @JvmStatic fun getInstance(itemsRemoteDataSource: ItemsDataSource,
                        itemsLocalDataSource: ItemsDataSource): ItemsRepository {

            return instance ?: ItemsRepository(itemsRemoteDataSource, itemsLocalDataSource)
                    .apply {instance = this}
        }

        @JvmStatic fun destroyInstance() {
            instance = null
        }
    }

    var cachedItems: LinkedHashMap<String, Item> = linkedMapOf()
    var cacheIsDirty = false

    override fun getItems(callback: ItemsDataSource.LoadItemsCallback) {
        if (cachedItems.isNotEmpty() && !cacheIsDirty) {
            callback.onItemsLoaded(ArrayList(cachedItems.values))
            return
        }

        if (cacheIsDirty){
            getItemsFromRemoteDataSource(callback)
        } else {
            // ローカルデータソースにあるかどうかを確認し、
            // なければリモートから取得する
            itemsLocalDataSource.getItems(object: ItemsDataSource.LoadItemsCallback {
                override fun onItemsLoaded(items: List<Item>) {
                    refreshCache(items)
                    callback.onItemsLoaded(ArrayList(cachedItems.values))
                }

                override fun onDataNotAvailable() {
                    getItemsFromRemoteDataSource(callback)
                }
            })
        }

    }

    private fun refreshCache(items: List<Item>) {
        cachedItems.clear()
        items.forEach{
            cacheAndPerform(it){}
        }
        cacheIsDirty = false
    }

    private inline fun cacheAndPerform(item: Item, perform: (Item) -> Unit) {
        val cachedItem = Item(
                item.rendered_body,
                item.body,
                item.coediting,
                item.comments_count,
                item.created_at,
                item.group,
                item.id,
                item.likes_count,
                item.private,
                item.reactions_count,
                item.tags,
                item.title,
                item.updated_at,
                item.url,
                item.user,
                item.page_views_count)
        cachedItems[cachedItem.id] = cachedItem
        perform(cachedItem)
    }

    private fun getItemsFromRemoteDataSource(callback: ItemsDataSource.LoadItemsCallback) {
        itemsRemoteDataSource.getItems(object : ItemsDataSource.LoadItemsCallback {
            override fun onItemsLoaded(items: List<Item>) {
                refreshCache(items)
                refreshLocalDataSource(items)
                callback.onItemsLoaded(ArrayList(cachedItems.values))
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

        })
    }

    private fun refreshLocalDataSource(items: List<Item>) {
        itemsLocalDataSource.deleteAllItems()
        for (item in items) {
            itemsLocalDataSource.saveItem(item)
        }
    }

    override fun getItem(itemId: String, callback: ItemsDataSource.GetItemCallback) {
        val cachedItem = getItemWithId(itemId)

        // キャッシュにあった場合
        if (cachedItem != null) {
            callback.onItemLoaded(cachedItem)
            return
        }

        // ローカルデータソースにあるかどうかを確認し、
        // なければリモートから取得する
        itemsLocalDataSource.getItem(itemId, object : ItemsDataSource.GetItemCallback {
            override fun onItemLoaded(item: Item) {
                cacheAndPerform(item) {
                    callback.onItemLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                itemsRemoteDataSource.getItem(itemId, object : ItemsDataSource.GetItemCallback {
                    override fun onItemLoaded(item: Item) {
                        cacheAndPerform(item) {
                            callback.onItemLoaded(it)
                        }
                    }

                    override fun onDataNotAvailable() {
                        // どこにもなかったため
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    private fun getItemWithId(id: String) = cachedItems[id]

    override fun refreshItems() {
        cacheIsDirty = true
    }

    override fun deleteAllItems() {
        // リモートデータソースは削除できない
        itemsLocalDataSource.deleteAllItems()
        cachedItems.clear()
    }

    override fun saveItem(item: Item) {
        cacheAndPerform(item) {
            // TODO: リモートデータソースにはまだ保存できない
            // itemsRemoteDataSource.saveItem(it)
            itemsLocalDataSource.saveItem(it)
        }
    }

}