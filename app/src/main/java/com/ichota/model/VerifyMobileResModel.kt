package com.ichota.model


import com.google.gson.annotations.SerializedName

data class VerifyMobileResModel(

    @SerializedName("success"      )
    val success: Int,
    @SerializedName("message_text" )
    val messageText: String,

   /* @SerializedName("data" )
    val data = OTPData*/

    @SerializedName("data"         ) var data        : Data?

)
data class Data (

    @SerializedName("phone" )
    val phone : String,
    @SerializedName("otp"   )
    val otp   : Int

)