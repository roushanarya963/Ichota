package com.ichota.model

import com.google.gson.annotations.SerializedName

data class UnReadChatMessageResModel(

    @SerializedName("success"      ) var success     : Int,
    @SerializedName("message_text" ) var messageText : String,
    @SerializedName("data"         ) var data        : DataChat

)
data class DataChat (

    @SerializedName("count" ) var count : String

)
