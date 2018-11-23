package jp.pules.exam.items.domain.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "items")
data class Item constructor(
        @ColumnInfo(name = "rendered_body") var rendered_body : String = "",
        @ColumnInfo(name = "body") var body : String = "",
        @ColumnInfo(name = "coediting") var coediting : Boolean = false,
        @ColumnInfo(name = "comments_count") var comments_count : Int = 0,
        @ColumnInfo(name = "created_at") var created_at : String = "",
        @ColumnInfo(name = "group") var group : String? = null,
        @PrimaryKey @ColumnInfo(name = "id") var id : String = "",
        @ColumnInfo(name = "likes_count") var likes_count : Int = 0,
        @ColumnInfo(name = "private") var private : Boolean = false,
        @ColumnInfo(name = "reactions_count") var reactions_count: Int = 0,
        @Ignore var tags : List<Tag> = listOf(),
        @ColumnInfo(name = "title") var title : String = "",
        @ColumnInfo(name = "updated_at") var updated_at : String = "",
        @ColumnInfo(name = "url") var url : String = "",
        @Ignore var user : User = User("", "", 0, 0, "", "", 0, "", "", "", "", 0, "", null, ""),
        @ColumnInfo(name = "page_views_count") var page_views_count : Int? = 0
) {

    val titleForList : String
        get() = if (title.isNotEmpty()) title else rendered_body

    val isEmpty : Boolean
        get() = title.isEmpty() && rendered_body.isEmpty()
}