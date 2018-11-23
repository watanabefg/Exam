package jp.pules.exam.items.domain.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity

@Entity(tableName = "tags")
data class Tag constructor(
        @ColumnInfo(name = "name") var name : String = "",
        @ColumnInfo(name = "versions") var versions: List<String>
)