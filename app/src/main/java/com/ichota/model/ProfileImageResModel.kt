package com.ichota.model

import com.google.gson.annotations.SerializedName

data class ProfileImageResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<ProfileImage>
)

data class ProfileImage(
    @SerializedName("user_image")
    val userImg: String

)