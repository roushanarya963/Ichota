package com.ichota.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val messageText: String,
    @SerializedName("data")
    val data: ArrayList<User>
)


