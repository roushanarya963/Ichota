package com.ichota.model

import com.google.gson.annotations.SerializedName

data class ServicePostsResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<ServicePost>
)


data class ServicePost(
    @SerializedName("id")
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("service_title")
    val serviceTitle: String?,
    @SerializedName("service_description")
    val serviceDescription: String?,
    @SerializedName("service_category")
    val serviceCategory: String?,
    @SerializedName("latitude")
    val latitude: String?,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("service_image")
    val serviceImage: String?,
    @SerializedName("isDeleted")
    val isDeleted: String,
    @SerializedName("service_categoryid")
    val serviceCategoryId: String,
    @SerializedName("service_cover_image" + "")
    val serviceCoverImg: String?,
    @SerializedName("post_type")
    val postType:String

)
