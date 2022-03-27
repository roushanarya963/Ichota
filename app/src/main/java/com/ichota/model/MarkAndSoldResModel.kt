package com.ichota.model

import com.google.gson.annotations.SerializedName

data class MarkAndSoldResModel(
    @SerializedName("success"      ) var success     : Int,
    @SerializedName("message_text" ) var messageText : String,
    @SerializedName("data"         ) var data        : ArrayList<UserChatData>
)

data class UserChatData(
    @SerializedName("user_image" ) var userImage : String,
    @SerializedName("user_name"  ) var userName  : String,
    @SerializedName("user_id"    ) var userId    : String
)