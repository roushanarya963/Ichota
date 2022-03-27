package com.ichota.model

import com.google.gson.annotations.SerializedName

data class SafetyBannersResModel(

    @SerializedName("success")
    val success:Int,
    @SerializedName("message_text")
    val message:String,
    @SerializedName("data")
    val safetyBanners: List<SafetyBanner>
    )


data class SafetyBanner(
    @SerializedName("id")
    val id : String,
    @SerializedName("title")
    val title:String,
    @SerializedName("description")
    val description:String,
    @SerializedName("image")
    val image:String
)
