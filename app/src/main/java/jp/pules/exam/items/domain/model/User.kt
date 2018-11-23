package jp.pules.exam.items.domain.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "users")
data class User constructor(
        @ColumnInfo(name = "description") var description: String,
        @ColumnInfo(name = "facebook_id") var facebook_id : String,
        @ColumnInfo(name = "followees_count") var followees_count : Int,
        @ColumnInfo(name = "followers_count") var followers_count : Int,
        @ColumnInfo(name = "github_login_name") var github_login_name : String,
        @PrimaryKey @ColumnInfo(name = "id") var id : String,
        @ColumnInfo(name = "item_count") var item_count : Int,
        @ColumnInfo(name = "linkedin_id") var linkedin_id : String,
        @ColumnInfo(name = "location") var location : String,
        @ColumnInfo(name = "name") var name : String,
        @ColumnInfo(name = "organization") var organization: String,
        @ColumnInfo(name = "permanent_id") var permanent_id : Int,
        @ColumnInfo(name = "profile_image_url") var profile_image_url: String,
        @ColumnInfo(name = "twitter_screen_name") var twitter_screen_name: String?,
        @ColumnInfo(name = "website_url") var website_url: String
)