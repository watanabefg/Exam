package jp.pules.exam.data.source.local

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import jp.pules.exam.data.source.ItemsDataSource
import jp.pules.exam.items.domain.model.Item
import jp.pules.exam.utils.SingleExecutors
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import java.util.*

@RunWith(AndroidJUnit4::class) @LargeTest
class ItemsLocalDataSourceTest {

    private val renderedbody = "rendered_body"
    private val body = "body"
    private val coediting = false
    private val commentsCount = 0
    private val createdAt = "date"
    private val group = ""
    private val id1 = "0"
    private val id2 = "2"
    private lateinit var localDataSource: ItemsLocalDataSource
    private lateinit var database: ExamDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                ExamDatabase::class.java)
                .build()

        ItemsLocalDataSource.clearInstance()
        localDataSource = ItemsLocalDataSource.getInstance(SingleExecutors(), database.itemsDao())
    }

    @After
    fun cleanUp() {
        database.close()
        ItemsLocalDataSource.clearInstance()
    }

    @Test
    fun testPreConditions() {
        assertNotNull(localDataSource)
    }

    @Test fun saveItem_retrievesItem() {
        val newItem = Item(renderedbody, body, coediting, commentsCount, createdAt, group, id1)

        with(localDataSource) {
            // 1件保存する
            saveItem(newItem)

            // そのIDを指定してgetItemを実行すると、同じItemがロードされること
            getItem(newItem.id, object : ItemsDataSource.GetItemCallback {
                override fun onItemLoaded(item: Item) {
                    assertThat(item, `is`(newItem))
                }

                override fun onDataNotAvailable() {
                    fail("Callback error")
                }
            })
        }
    }

    @Test fun deleteAllItems_emptyListOfRetrievedItem() {
        with(localDataSource) {
            saveItem(Item(renderedbody, body, coediting, commentsCount, createdAt, group, id1))
            val callback = mock(ItemsDataSource.LoadItemsCallback::class.java)

            deleteAllItems()

            getItems(callback)

            // 0件取得となること
            verify(callback).onDataNotAvailable()
            verify(callback, never()).onItemsLoaded(LinkedList<Item>())
        }
    }

    @Test fun getItems_retrieveSavedItems() {
        with(localDataSource) {
            val newItem1 = Item(renderedbody, body, coediting, commentsCount, createdAt, group, id1)
            saveItem(newItem1)
            val newItem2 = Item(renderedbody, body, coediting, commentsCount, createdAt, group, id2)
            saveItem(newItem2)

            getItems(object : ItemsDataSource.LoadItemsCallback {
                override fun onItemsLoaded(items: List<Item>) {
                    // インサートした2件が存在すること
                    assertNotNull(items)
                    assertTrue(items.size >= 2)

                    var newItem1IdFound = false
                    var newItem2IdFound = false
                    for (item in items) {
                        if (item.id == newItem1.id) {
                            newItem1IdFound = true
                        }
                        if (item.id == newItem2.id) {
                            newItem2IdFound = true
                        }
                    }
                    assertTrue(newItem1IdFound)
                    assertTrue(newItem2IdFound)
                }

                override fun onDataNotAvailable() {
                    fail()
                }
            })
        }
    }
}