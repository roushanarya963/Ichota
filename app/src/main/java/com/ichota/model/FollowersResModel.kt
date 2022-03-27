package com.ichota.model

import com.google.gson.annotations.SerializedName

data class FollowersResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<User>
)