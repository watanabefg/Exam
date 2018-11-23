package jp.pules.exam.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import jp.pules.exam.items.domain.model.Item

@Dao
interface ItemsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertItem(item: Item)

    @Query("SELECT * FROM Items")
    fun getItems(): List<Item>

    @Query("SELECT * FROM Items WHERE id = :id")
    fun getItemById(id: String): Item?

    @Query("DELETE FROM Items")
    fun deleteItems()

}