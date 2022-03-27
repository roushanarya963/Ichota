package com.ichota.model

import com.google.gson.annotations.SerializedName

data class ServiceResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<ServiceCategory>
)


data class ServiceCategory(
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("membership_status")
    val membershipStatus : String,
    @SerializedName("isDeleted")
    val isDeleted:String

)