package com.ichota.model

import com.google.gson.annotations.SerializedName

data class MarketPlaceResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<MarketPlacePost>

)

data class MarketPlacePost(
    @SerializedName("id")
    val id: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("product_description")
    val productDescription: String,
    @SerializedName("product_price")
    val productPrice: String,
    @SerializedName("product_cover_img")
    val productCoverImage: String,
    @SerializedName("user_id")
    val userId : String,
    @SerializedName("Buying_option")
    val buyingOptions : String,
    @SerializedName("post_type")
    val postType : String

)
