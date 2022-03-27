package com.ichota.model

import com.google.gson.annotations.SerializedName

data class ToggleFollowUnfollowResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message : String,
    @SerializedName("data")
    val data : Int

)