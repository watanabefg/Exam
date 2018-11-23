package jp.pules.exam.data.source.local

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import jp.pules.exam.items.domain.model.Item
import jp.pules.exam.items.domain.model.Tag
import jp.pules.exam.items.domain.model.User
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) class ItemsDaoTest {
    private lateinit var database: ExamDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                ExamDatabase::class.java).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertItemAndGetById() {
        // 1件インサートしておく
        database.itemsDao().insertItem(DEFAULT_ITEM)

        // ID指定で取得すると、セットした値が入っていること
        val loaded = database.itemsDao().getItemById(DEFAULT_ITEM.id)
        assertItem(loaded, DEFAULT_ID, DEFAULT_TITLE, DEFAULT_RENDEREDBODY)
    }

    @Test fun insertItemReplacesOnConflict() {
        database.itemsDao().insertItem(DEFAULT_ITEM)

        // 同じIDでインサートし直す
        val newItem = Item(NEW_RENDEREDBODY, "body1", false, 0, "", "", DEFAULT_ID, 0, false, 0, DEFAULT_TAG, NEW_TITLE, "", "", DEFAULT_USER, 0)
        database.itemsDao().insertItem(newItem)

        // titleとrenderedbodyが変わっていること
        val loaded = database.itemsDao().getItemById(DEFAULT_ITEM.id)
        assertItem(loaded, DEFAULT_ID, NEW_TITLE, NEW_RENDEREDBODY)
    }

    @Test fun insertItemAndGetItems() {
        // 1件インサートしておく
        database.itemsDao().insertItem(DEFAULT_ITEM)

        // 全件取得すると
        val items = database.itemsDao().getItems()

        // 件数が1件であること
        assertThat(items.size, `is`(1))

        // 取得したデータがセットした値であること
        assertItem(items[0], DEFAULT_ID, DEFAULT_TITLE, DEFAULT_RENDEREDBODY)
    }

    @Test fun deleteItemsAndGettingItems() {
        database.itemsDao().insertItem(DEFAULT_ITEM)

        // 全件削除
        database.itemsDao().deleteItems()

        // 全件取得すると
        val items = database.itemsDao().getItems()

        // 件数が0であること
        assertThat(items.size, `is`(0))
    }

    private fun assertItem(
            item: Item?,
            id: String,
            title: String,
            renderedBody: String
    ) {
        assertThat<Item>(item as Item, notNullValue())
        assertThat(item.id, `is`(id))
        assertThat(item.title, `is`(title))
        assertThat(item.rendered_body, `is`(renderedBody))
    }
    companion object {
        private val DEFAULT_TITLE = "title"
        private val DEFAULT_RENDEREDBODY = "renderedbody"
        private val DEFAULT_ID = "id"
        private val DEFAULT_TAG = listOf(
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

        private val DEFAULT_USER = User(
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

        private val DEFAULT_ITEM = Item(DEFAULT_RENDEREDBODY,
                "body1",
                false,
                0,
                "2018-11-21T12:50:40+09:00",
                null,
                DEFAULT_ID,
                0,
                false,
                0,
                DEFAULT_TAG,
                DEFAULT_TITLE,
                "2018-11-21T12:50:40+09:00",
                "https://example.com/1",
                DEFAULT_USER,
                null
        )

        private val NEW_TITLE = "title2"
        private val NEW_RENDEREDBODY = "renderedbody2"
    }
}
