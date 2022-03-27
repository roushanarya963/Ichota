package com.ichota.model

import com.google.gson.annotations.SerializedName

class ForgotPasswordRes(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String
)