package jp.pules.exam.itemdetail

import android.content.Intent
import android.support.test.espresso.NoActivityResumedException
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import jp.pules.exam.TestUtils
import jp.pules.exam.items.domain.model.Item
import jp.pules.exam.data.source.ItemsRepository
import jp.pules.exam.items.domain.model.Tag
import jp.pules.exam.items.domain.model.User
import junit.framework.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the items screen, the main screen which contains a list of all items.
 */
@RunWith(AndroidJUnit4::class) @LargeTest class ItemDetailScreenTest {

    private val title = "ATSL"
    private val renderedbody = "Rocks"
    private val id = "id"

    private val tag = listOf(
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

    private val item1 = Item(title,
            renderedbody,
            false,
            0,
            "2018-11-21T12:50:40+09:00",
            null,
            id,
            0,
            false,
            0,
            tag,
            title,
            "2018-11-21T12:50:40+09:00",
            "https://example.com/1",
            user,
            null
    )

    @Rule @JvmField var itemDetailActivityTestRule =
            ActivityTestRule(ItemDetailActivity::class.java, true, false)

    private fun loadItem() {
        startActivityWithWithStubbedItem(item1)
    }

    private fun startActivityWithWithStubbedItem(item: Item) {
        ItemsRepository.destroyInstance()
        // TODO: addItemの実装後に直す
        //FakeItemsRemoteDataSource.getInstance().addItems(item)

        val startIntent = Intent().apply { putExtra(ItemDetailActivity.EXTRA_ITEM_ID, item.id) }
        itemDetailActivityTestRule.launchActivity(startIntent)
    }

    @Test fun activeItemDetails_DisplayedInUi() {
        loadItem()

        // TODO: addItemの実装後にテストする
        // 指定したtitle, renderedbodyが表示されていること
        //onView(withId(R.id.item_detail_title)).check(matches(withText(title)))
        //onView(withId(R.id.item_detail_renderedbody)).check(matches(withText(renderedbody)))
    }

    @Test fun orientationChange_itemPersist() {
        loadItem()

        // 画面の向きを変えてもクラッシュしないこと
        try {
            TestUtils.rotateOrientation(itemDetailActivityTestRule.activity)
            // Test OK
        } catch (e: NoActivityResumedException) {
            fail("crash")
        }

        // TODO: addItemの実装後にテストする
        // 指定したtitle, renderedbodyが表示されていること
        //onView(withId(R.id.item_detail_title)).check(matches(withText(title)))
        //onView(withId(R.id.item_detail_renderedbody)).check(matches(withText(renderedbody)))
    }

}