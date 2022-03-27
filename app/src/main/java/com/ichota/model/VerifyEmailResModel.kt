package com.ichota.model

import com.google.gson.annotations.SerializedName

data class VerifyEmailResModel(
        @SerializedName("success")
        val success : Int,
        @SerializedName("message_text")
        val message : String
)
