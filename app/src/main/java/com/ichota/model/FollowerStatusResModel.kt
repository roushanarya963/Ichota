package com.ichota.model

import com.google.gson.annotations.SerializedName

data class FollowerStatusResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val followerStatus: ArrayList<FollowerStatus>
)

data class FollowerStatus(
    @SerializedName("status")
    val status: String
)