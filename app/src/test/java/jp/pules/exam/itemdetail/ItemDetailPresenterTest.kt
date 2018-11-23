package jp.pules.exam.itemdetail

import jp.pules.exam.TestUseCaseScheduler
import jp.pules.exam.UseCaseHandler
import jp.pules.exam.capture
import jp.pules.exam.data.source.ItemsDataSource
import jp.pules.exam.data.source.ItemsRepository
import jp.pules.exam.itemdetail.domain.usecase.GetItem
import jp.pules.exam.items.domain.model.Item
import jp.pules.exam.items.domain.model.Tag
import jp.pules.exam.items.domain.model.User
import jp.pules.exam.safeEq
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.*


/**
 * Unit tests for the implementation of [ItemDetailPresenter]
 */
class ItemDetailPresenterTest {

    val tags = listOf(
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

    val idTest = "id"
    val titleTest = "title"
    val renderedbodyTest = "renderedBody"
    val invalidItemId = ""
    var activeItem = Item(renderedbodyTest,
            "body1",
            false,
            0,
            "2018-11-21T12:50:40+09:00",
            null,
            idTest,
            0,
            false,
            0,
            tags,
            titleTest,
            "2018-11-21T12:50:40+09:00",
            "https://example.com/1",
            user,
            null
    )

    @Mock private lateinit var itemsRepository: ItemsRepository
    @Mock private lateinit var itemDetailView: ItemDetailContract.View
    @Captor private lateinit var getItemCallbackCaptor: ArgumentCaptor<ItemsDataSource.GetItemCallback>
    private lateinit var itemDetailPresenter: ItemDetailPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        // The presenter won't update the view unless it's active.
        `when`(itemDetailView.isActive).thenReturn(true)
    }

    @Test
    fun createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test
        itemDetailPresenter = givenItemDetailPresenter(activeItem.id)

        // Then the presenter is set to the view
        verify(itemDetailView).setPresenter(itemDetailPresenter)
    }

    private fun givenItemDetailPresenter(id: String): ItemDetailPresenter {
        val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())
        val getItem = GetItem(itemsRepository)

        return ItemDetailPresenter(useCaseHandler, id, itemDetailView, getItem)
    }

    @Test
    fun getActiveItemFromRepositoryAndLoadIntoView() {
        // When items presenter is asked to open a item
        itemDetailPresenter = givenItemDetailPresenter(activeItem.id).apply { start() }

        // TODO: 下記のエラーの回避策としてMockitoKotlinHelpersにsafeEqを記載したがこれで良いのかよくわかっていない
        // java.lang.IllegalStateException: eq(activeItem.id) must not be null
        //
        // 参考) https://github.com/mockito/mockito/issues/1255#issuecomment-346822428
        verify(itemsRepository).getItem(safeEq(activeItem.id), capture(getItemCallbackCaptor))

        val inOrder = inOrder(itemDetailView)
        inOrder.verify<ItemDetailContract.View>(itemDetailView).setLoadingIndicator(true)

        // When item is finally loaded
        getItemCallbackCaptor.value.onItemLoaded(activeItem) // Trigger callback

        // Then progress indicator is hidden and title, renderedBody and completion status are shown
        // in UI
        inOrder.verify<ItemDetailContract.View>(itemDetailView).setLoadingIndicator(false)
        verify(itemDetailView).showTitle(titleTest)
        verify(itemDetailView).showRenderedBody(renderedbodyTest)
    }

    @Test
    fun getUnknownItemFromRepositoryAndLoadIntoView() {
        // 存在しないIDの記事を開こうとしたら、存在しないとわかること
        itemDetailPresenter = givenItemDetailPresenter(invalidItemId)
        itemDetailPresenter.start()
        verify(itemDetailView).showMissingItem()
    }


}