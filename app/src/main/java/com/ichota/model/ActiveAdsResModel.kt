package com.ichota.model

import com.google.gson.annotations.SerializedName

data class ActiveAdsResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<ActivePost>
)

data class ActivePost(
    @SerializedName("post_id")
    val postId: String,
    @SerializedName("post_name")
    val postName: String,
    @SerializedName("post_description")
    val postDescription: String,
    @SerializedName("product_price")
    val productPrice: String,
    @SerializedName("buying_option")
    val buyingOptions : String,
    @SerializedName("user_id")
    val userId : String,
    @SerializedName("cover_image")
    val coverImage: String,
    @SerializedName("post_type")
    val postType : String,
    @SerializedName("active_sold_status")
    val activeSoldStatus :String,
    @SerializedName("createdDtm")
    val createdDtm: String
)
