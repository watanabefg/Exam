package jp.pules.exam.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import jp.pules.exam.items.domain.model.Item

// Itemsテーブルを含んだRoom DB
@Database(entities = arrayOf(Item::class), version = 1)
abstract class ExamDatabase : RoomDatabase() {
    abstract fun itemsDao() : ItemsDao

    companion object {
        private var INSTANCE: ExamDatabase? = null
        private const val fileName = "exam.db"
        private val lock = Any()

        fun getInstance(context: Context): ExamDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            ExamDatabase::class.java,
                            fileName)
                            .build()
                }
                return INSTANCE!!
            }
        }
    }
}