package com.ichota.model

import com.google.gson.annotations.SerializedName

data class UserReviewResModel(
    @SerializedName("success") var success : Int,
    @SerializedName("message_text") var messageText : String,
    @SerializedName("data") var data : RecordData
)

data class RecordData (

    @SerializedName("total_user_rating") var totalUserRating : String,
    @SerializedName("total_communication_rating") var totalCommunicationRating : String,
    @SerializedName("total_on_time_delivery_rating") var totalOnTimeDeliveryRating : String,
    @SerializedName("total_item_as_described_rating") var totalItemAsDescribedRating : String,
    @SerializedName("total_reliable_rating") var totalReliableRating : String,
    @SerializedName("records") var records : List<Record>

)

data class Record (

    @SerializedName("comment") var comment : String,
    @SerializedName("time") var time : String,
    @SerializedName("user_image") var userImage : String,
    @SerializedName("user_name") var userName : String,
    @SerializedName("user_rating") var userRating : Double

)