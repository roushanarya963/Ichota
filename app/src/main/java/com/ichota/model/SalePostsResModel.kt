package com.ichota.model

import com.google.gson.annotations.SerializedName

data class SalePostsResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<SalePost>
)


data class SalePost(
    @SerializedName("id")
    val id: String,
    @SerializedName("product_img")
    val productImg: String?,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("product_description")
    val productDescription: String?,
    @SerializedName("product_price")
    val productPrice: String?,
    @SerializedName("firm_price_status")
    val firmPriceStatus: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("Buying_option")
    val buyingOptions: String?,
    @SerializedName("conditions")
    val conditions: String,
    @SerializedName("latitude")
    val lattitude: String?,
    @SerializedName("longitude")
    val longitude: String?,
    @SerializedName("sell_ship_nation_status")
    val sellShipNationStatus: String?,
    @SerializedName("select_box_size")
    val selectBoxSize: String?,
    @SerializedName("isDeleted")
    val isDeleted: String?,
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("product_cover_img")
    val productCoverImg: String?,
    @SerializedName("distance")
    val distance: String?,
    @SerializedName("active_sold_status")
    val activeSoldStatus: String?,
    @SerializedName("post_type")
    val postType:String


)
