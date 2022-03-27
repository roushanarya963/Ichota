package com.ichota.model

import com.google.gson.annotations.SerializedName

data class SearchResModel(
    @SerializedName("success")
    val success : Int,
    @SerializedName("message_text")
    val message :String,
    @SerializedName("data")
    val searchPosts : List<SearchPost>
)

data class SearchPost(
    @SerializedName("post_id")
    val postId : String,
    @SerializedName("post_name")
    val postName : String,
    @SerializedName("post_description")
    val postDescription : String,
    @SerializedName("buying_option")
    val buyingOptions : String,
    @SerializedName("user_id")
    val userId : String,
    @SerializedName("cover_image")
    val coverImage : String,
    @SerializedName("post_type")
    val postType : String
)
