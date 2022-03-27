package com.ichota.model

import com.google.gson.annotations.SerializedName

data class TotalUnseenNotificationResModel(
    @SerializedName("success"      ) var success     : Int,
    @SerializedName("message_text" ) var messageText : String,
    @SerializedName("data"         ) var data        : TotalUnseenNotification
)
data class TotalUnseenNotification(
    @SerializedName("totalUnseenNotification" ) var totalUnseenNotification : String
)