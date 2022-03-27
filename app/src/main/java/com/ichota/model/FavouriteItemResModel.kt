package com.ichota.model

import com.google.gson.annotations.SerializedName

data class FavouriteItemResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<FavouriteItem>
)

data class FavouriteItem(

    /*@SerializedName("id")
    val id: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("product_description")
    val productDescription: String,
    @SerializedName("product_id")
    val productId : String,
    @SerializedName("product_cover_img")
    val productCoverImage: String*/

    @SerializedName("id"                ) var id              : String,
    @SerializedName("user_id"           ) var userId          : String,
    @SerializedName("product_id"        ) var productId       : String,
    @SerializedName("status"            ) var status          : String,
    @SerializedName("product_cover_img" ) var productCoverImg : String,
    @SerializedName("post_type"         ) var postType        : String,

)