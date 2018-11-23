package jp.pules.exam.data.source

import jp.pules.exam.any
import jp.pules.exam.capture
import jp.pules.exam.items.domain.model.Item
import jp.pules.exam.eq
import jp.pules.exam.items.domain.model.Tag
import jp.pules.exam.items.domain.model.User
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class ItemsRepositoryTest {

    private val tags = listOf(
            Tag(
              "タグ",
                    listOf()
            ),
            Tag(
                    "タグ2",
                    listOf()
            ),
            Tag(
                    "タグ3",
                    listOf()
            )
    )
    private val tags2 = listOf(
            Tag(
                    "タグ4",
                    listOf()
            ),
            Tag(
                    "タグ5",
                    listOf()
            ),
            Tag(
                    "タグ6",
                    listOf()
            )
    )

    private val user = User(
            "User description",
            "",
            0,
            0,
            "github login name",
            "user id",
            1,
            "",
            "",
            "",
            "",
            1,
            "https://example.com/profile-image/",
            null,
            ""
    )
    private val user2 = User(
            "User description2",
            "facebook id",
            0,
            0,
            "github login name2",
            "user id2",
            2,
            "",
            "",
            "user name2",
            "organization 2",
            2,
            "https://example.com/profile-image-2/",
            "twitter screen name",
            "https://example.com/"
    )

    private val itemId1 = "abc"
    private val itemId2 = "def"
    private val items = listOf(
            Item("renderedbody1",
                    "body1",
                    false,
                    0,
                    "2018-11-21T12:50:40+09:00",
                    null,
                    itemId1,
                    0,
                    false,
                    0,
                    tags,
                    "title1",
                    "2018-11-21T12:50:40+09:00",
                    "https://example.com/1",
                    user,
                    null
            ),
            Item("renderedbody2",
                    "body2",
                    false,
                    0,
                    "2018-11-20T10:40:40+09:00",
                    null,
                    itemId2,
                    1,
                    false,
                    1,
                    tags2,
                    "title1",
                    "2018-11-21T12:50:40+09:00",
                    "https://example.com/1",
                    user2,
                    1
            )
    )

    private lateinit var itemsRepository: ItemsRepository

    @Mock private lateinit var itemsRemoteDataSource: ItemsDataSource
    @Mock private lateinit var itemsLocalDataSource: ItemsDataSource
    @Mock private lateinit var getItemCallback: ItemsDataSource.GetItemCallback
    @Mock private lateinit var loadItemsCallback: ItemsDataSource.LoadItemsCallback

    @Captor private lateinit var itemsCallbackCaptor: ArgumentCaptor<ItemsDataSource.LoadItemsCallback>
    @Captor private lateinit var itemCallbackCaptor: ArgumentCaptor<ItemsDataSource.GetItemCallback>

    @Before
    fun setupItemsRepository() {
        MockitoAnnotations.initMocks(this)

        itemsRepository = ItemsRepository.getInstance(itemsRemoteDataSource,
                itemsLocalDataSource)
    }

    @After
    fun destroyRepositoryInstance() {
        ItemsRepository.destroyInstance()
    }

    @Test fun getItems_repositoryCachesAfterFirstApiCall() {
        twoItemsLoadCallsToRepository(loadItemsCallback)

        verify(itemsRemoteDataSource).getItems(any<ItemsDataSource.LoadItemsCallback>())
    }

    private fun twoItemsLoadCallsToRepository(callback : ItemsDataSource.LoadItemsCallback) {
        itemsRepository.getItems(callback) // APIの初回コール

        // ローカルデータソースが呼び出されることを検証
        verify(itemsLocalDataSource).getItems(capture(itemsCallbackCaptor))

        // ローカルデータソースにデータがまだない
        itemsCallbackCaptor.value.onDataNotAvailable()

        // リモートデータソースが呼び出されることを検証
        verify(itemsRemoteDataSource).getItems(capture(itemsCallbackCaptor))

        itemsCallbackCaptor.value.onItemsLoaded(items)

        itemsRepository.getItems(callback) // APIの2回目のコール
    }

    @Test fun getItems_requestsAllItemsFromLocalDataSource() {
        itemsRepository.getItems(loadItemsCallback) // itemsをリクエスト

        // ローカルデータソースからitemsがロードされる
        verify(itemsLocalDataSource).getItems(any<ItemsDataSource.LoadItemsCallback>())
    }

    @Test fun getItem_requestsSingleItemFromLocalDataSource() {
        itemsRepository.getItem(itemId1, getItemCallback)

        // Then the task is loaded from the database
        verify(itemsLocalDataSource).getItem(eq(itemId1), any<
                ItemsDataSource.GetItemCallback>())
    }

    @Test fun saveItem_saveItemToLocalDataSource() {
        val newItem = Item("renderedbody3",
                "body3",
                false,
                1,
                "2018-11-10T10:40:40+09:00",
                null,
                "3",
                1,
                false,
                1,
                tags2,
                "title1",
                "2018-11-21T12:50:40+09:00",
                "https://example.com/1",
                user2,
                1
        )

        itemsRepository.saveItem(newItem)

        // リモートには保存しないので検証しない
        verify(itemsLocalDataSource).saveItem(newItem)
        assertThat(itemsRepository.cachedItems.size, `is`(1))
    }

    @Test fun deleteAllItems_deleteItemsToLocalDataSourceUpdatesCache() {
        with(itemsRepository) {
            val newItem = Item("renderedbody1",
                    "body1",
                    false,
                    0,
                    "2018-11-21T12:50:40+09:00",
                    null,
                    itemId1,
                    0,
                    false,
                    0,
                    tags,
                    "title1",
                    "2018-11-21T12:50:40+09:00",
                    "https://example.com/1",
                    user,
                    null
            )
            saveItem(newItem)

            val newItem2 = Item("renderedbody2",
                    "body2",
                    false,
                    0,
                    "2018-11-20T10:40:40+09:00",
                    null,
                    itemId2,
                    1,
                    false,
                    1,
                    tags2,
                    "title1",
                    "2018-11-21T12:50:40+09:00",
                    "https://example.com/1",
                    user2,
                    1
            )
            saveItem(newItem2)

            deleteAllItems()

            // リモートデータソースは外部APIで削除しないため検証しない
            verify(itemsLocalDataSource).deleteAllItems()

            assertThat(cachedItems.size, `is`(0))
        }
    }

    @Test fun getItemsWithDirtyCache_itemsAreRetrievedFromRemote() {
        with(itemsRepository) {
            refreshItems()
            getItems(loadItemsCallback)
        }

        setItemsAvailable(itemsRemoteDataSource, items)

        // ローカルからではなく、リモートから記事一覧が習得できること
        verify(itemsLocalDataSource, never()).getItems(loadItemsCallback)
        verify(loadItemsCallback).onItemsLoaded(items)
    }

    private fun setItemsAvailable(dataSource: ItemsDataSource, items: List<Item>) {
        verify(dataSource).getItems(capture(itemsCallbackCaptor))
        itemsCallbackCaptor.value.onItemsLoaded(items)
    }

    @Test fun getItemsWithLocalDataSourceUnavailable_itemsAreRetrievedFromRemote() {
        itemsRepository.getItems(loadItemsCallback)

        setItemsNotAvailable(itemsLocalDataSource)

        setItemsAvailable(itemsRemoteDataSource, items)

        verify(loadItemsCallback).onItemsLoaded(items)
    }

    @Test fun getItemsWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        itemsRepository.getItems(loadItemsCallback)

        // ローカルからもリモートからも取得できない状態にする
        setItemsNotAvailable(itemsLocalDataSource)
        setItemsNotAvailable(itemsRemoteDataSource)

        verify(loadItemsCallback).onDataNotAvailable()
    }

    private fun setItemsNotAvailable(dataSource: ItemsDataSource) {
        verify(dataSource).getItems(capture(itemsCallbackCaptor))
        itemsCallbackCaptor.value.onDataNotAvailable()
    }

    // 単一記事の場合
    @Test fun getItemWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        val itemId = "abc"
        itemsRepository.getItem(itemId, getItemCallback)

        // ローカルからもリモートからも取得できない状態にする
        setItemNotAvailable(itemsLocalDataSource, itemId)
        setItemNotAvailable(itemsRemoteDataSource, itemId)

        verify(getItemCallback).onDataNotAvailable()
    }

    private fun setItemNotAvailable(dataSource: ItemsDataSource, itemId: String) {
        verify(dataSource).getItem(eq(itemId), capture(itemCallbackCaptor))
        itemCallbackCaptor.value.onDataNotAvailable()
    }

    @Test fun getItems_refreshesLocalDataSource() {
        with(itemsRepository) {
            refreshItems()
            getItems(loadItemsCallback)
        }

        setItemsAvailable(itemsRemoteDataSource, items)

        // リモートから取得したデータが同じ件数分ローカルに保存されること
        verify(itemsLocalDataSource, times(items.size)).saveItem(any<Item>())
    }

}