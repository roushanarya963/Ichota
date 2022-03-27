package com.ichota.model

import com.google.gson.annotations.SerializedName

data class SaleResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<SaleCategory>
)


data class SaleCategory(

    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("category_img")
    val categoryImg: String
)

