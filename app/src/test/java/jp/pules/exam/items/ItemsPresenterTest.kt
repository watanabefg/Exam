package jp.pules.exam.items

import jp.pules.exam.TestUseCaseScheduler
import jp.pules.exam.UseCaseHandler
import jp.pules.exam.argumentCaptor
import jp.pules.exam.capture
import jp.pules.exam.data.source.ItemsDataSource
import jp.pules.exam.items.domain.model.Item
import jp.pules.exam.data.source.ItemsRepository
import jp.pules.exam.items.domain.model.Tag
import jp.pules.exam.items.domain.model.User
import jp.pules.exam.items.domain.usecase.GetItems
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.*


class ItemsPresenterTest {

    @Mock private lateinit var itemsRepository: ItemsRepository
    @Mock private lateinit var itemsView: ItemsContract.View
    @Captor private lateinit var loadItemsCallbackCaptor: ArgumentCaptor<ItemsDataSource.LoadItemsCallback>
    private lateinit var itemsPresenter: ItemsPresenter
    private lateinit var items : MutableList<Item>
    private var noItems : MutableList<Item>? = null

    private val itemId1 = "abc"
    private val itemId2 = "def"
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

    @Before
    fun setupItemsPresenter() {
        MockitoAnnotations.initMocks(this)

        itemsPresenter = givenItemsPresenter()

        `when`(itemsView.isActive).thenReturn(true)

        items = mutableListOf(
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
                ),
                Item("renderedbody3",
                        "body3",
                        false,
                        0,
                        "2018-11-10T12:50:40+09:00",
                        null,
                        "AAAA3",
                        0,
                        false,
                        0,
                        tags,
                        "title3",
                        "2018-11-21T12:50:40+09:00",
                        "https://example.com/1",
                        user,
                        null
                ),
                Item("renderedbody4",
                        "body4",
                        false,
                        0,
                        "2018-11-20T10:40:40+09:00",
                        null,
                        "aaaaa4",
                        1,
                        false,
                        1,
                        tags2,
                        "title1",
                        "2018-11-21T12:50:40+09:00",
                        "https://example.com/1",
                        user2,
                        5
                )
        )
        noItems =  mutableListOf()
    }

    private fun givenItemsPresenter(): ItemsPresenter {
        val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())
        val getItems = GetItems(itemsRepository)

        return ItemsPresenter(useCaseHandler, itemsView, getItems)
    }

    @Test
    fun createPresenter_setsThePresenterToView() {
        itemsPresenter = givenItemsPresenter()

        verify(itemsView).setPresenter(itemsPresenter)
    }

    @Test
    fun loadNoItems() {
        itemsPresenter.loadItems(true)

        verify(itemsRepository).getItems(capture(loadItemsCallbackCaptor))
        loadItemsCallbackCaptor.value.onItemsLoaded(noItems!!)

        // ローディングインディケーターが表示される
        val inOrder = inOrder(itemsView)
        inOrder.verify(itemsView).setLoadingIndicator(true)

        // その後ローディングインディケーターが隠れる
        inOrder.verify(itemsView).setLoadingIndicator(false)

        // 0件であること
        verify(itemsView).showNoItems()
    }

    @Test
    fun loadAllItemsFromRepositoryAndLoadIntoView() {
        itemsPresenter.loadItems(true)

        verify(itemsRepository).getItems(capture(loadItemsCallbackCaptor))
        loadItemsCallbackCaptor.value.onItemsLoaded(items)

        // ローディングインディケーターが表示される
        val inOrder = inOrder(itemsView)
        inOrder.verify(itemsView).setLoadingIndicator(true)

        // その後ローディングインディケーターが隠れる
        inOrder.verify(itemsView).setLoadingIndicator(false)

        val showItemsArgumentCaptor = argumentCaptor<List<Item>>()
        verify(itemsView).showItems(capture(showItemsArgumentCaptor))

        // 4件あること
        assertThat(showItemsArgumentCaptor.value.size, `is`(4))
    }

    @Test
    fun clickOnItem_showsDetailUi() {
        val requestedItem = Item("renderedbody1",
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

        // open item detailsを呼び出す
        itemsPresenter.openItemDetails(requestedItem)
        verify(itemsView).showItemDetailsUi(itemId1)
    }

    @Test
    fun unavailableItems_showsError() {
        itemsPresenter.loadItems(true)

        verify(itemsRepository).getItems(capture(loadItemsCallbackCaptor))
        loadItemsCallbackCaptor.value.onDataNotAvailable()

        verify(itemsView).showLoadingItemsError()
    }

}