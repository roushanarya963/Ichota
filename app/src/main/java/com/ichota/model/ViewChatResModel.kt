package com.ichota.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ViewChatResModel(

    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ChatData,

)


class ChatData(
    @SerializedName("chat_list"      ) var chatList       : ArrayList<Message>       = arrayListOf(),
    @SerializedName("productDetails" ) var postDetails    : ArrayList<PostDetail> = arrayListOf(),
    @SerializedName("users_status"   ) var usersStatus    : ArrayList<UsersStatus>    = arrayListOf()
):Parcelable{
    constructor(parcel: Parcel) : this(
        TODO("chatList"),
        TODO("productDetails"),
        TODO("usersStatus")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatData> {
        override fun createFromParcel(parcel: Parcel): ChatData {
            return ChatData(parcel)
        }

        override fun newArray(size: Int): Array<ChatData?> {
            return arrayOfNulls(size)
        }
    }

}


data class Message(
    @SerializedName("sender_id"      ) var senderId      : String? ,
    @SerializedName("sender_name"    ) var senderName    : String? ,
    @SerializedName("sender_image"   ) var senderImage   : String? ,
    @SerializedName("receiver_id"    ) var receiverId    : String? ,
    @SerializedName("receiver_name"  ) var receiverName  : String? ,
    @SerializedName("receiver_image" ) var receiverImage : String? ,
    @SerializedName("message"        ) var message       : String? ,
    @SerializedName("status"         ) var status        : String? ,
    @SerializedName("msg_type"       ) var msgType       : String? ,
    @SerializedName("msg_time"       ) var msgTime       : String? ,
    @SerializedName("lat"            ) var lat           : String? ,
    @SerializedName("lng"            ) var lng           : String? ,
    @SerializedName("sent_image"     ) var sentImage     : String? 

):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(senderId)
        parcel.writeString(senderName)
        parcel.writeString(senderImage)
        parcel.writeString(receiverId)
        parcel.writeString(receiverName)
        parcel.writeString(receiverImage)
        parcel.writeString(message)
        parcel.writeString(status)
        parcel.writeString(msgType)
        parcel.writeString(msgTime)
        parcel.writeString(lat)
        parcel.writeString(lng)
        parcel.writeString(sentImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }

}

class PostDetail(
    @SerializedName("id"                      ) var id                   : String? ,
    @SerializedName("product_img"             ) var productImg           : String? ,
    @SerializedName("product_name"            ) var productName          : String? ,
    @SerializedName("product_description"     ) var productDescription   : String? ,
    @SerializedName("product_price"           ) var productPrice         : String? ,
    @SerializedName("firm_price_status"       ) var firmPriceStatus      : String? ,
    @SerializedName("category"                ) var category             : String? ,
    @SerializedName("Buying_option"           ) var BuyingOption         : String? ,
    @SerializedName("conditions"              ) var conditions           : String? ,
    @SerializedName("latitude"                ) var latitude             : String? ,
    @SerializedName("longitude"               ) var longitude            : String? ,
    @SerializedName("sell_ship_nation_status" ) var sellShipNationStatus : String? ,
    @SerializedName("select_box_size"         ) var selectBoxSize        : String? ,
    @SerializedName("isDeleted"               ) var isDeleted            : String? ,
    @SerializedName("createdDtm"              ) var createdDtm           : String? ,
    @SerializedName("updatedDtm"              ) var updatedDtm           : String? ,
    @SerializedName("category_id"             ) var categoryId           : String? ,
    @SerializedName("user_id"                 ) var userId               : String? ,
    @SerializedName("image_path"              ) var imagePath            : String? ,
    @SerializedName("product_cover_img"       ) var productCoverImg      : String? ,
    @SerializedName("distance"                ) var distance             : String? ,
    @SerializedName("fav_status"              ) var favStatus            : String? ,
    @SerializedName("active_sold_status"      ) var activeSoldStatus     : String? ,
    @SerializedName("brand_name"              ) var brandName            : String? ,
    @SerializedName("year"                    ) var year                 : String? ,
    @SerializedName("fuel_type"               ) var fuelType             : String? ,
    @SerializedName("transmission_type"       ) var transmissionType     : String? ,
    @SerializedName("km_driven"               ) var kmDriven             : String? ,
    @SerializedName("mileage"                 ) var mileage              : String? ,
    @SerializedName("no_of_owner"             ) var noOfOwner            : String? ,
    @SerializedName("car_title"               ) var carTitle             : String? ,
    @SerializedName("car_additional_info"     ) var carAdditionalInfo    : String? ,
    @SerializedName("bid_start_time"          ) var bidStartTime         : String? ,
    @SerializedName("bid_end_time"            ) var bidEndTime           : String? ,
    @SerializedName("vin_number"              ) var vinNumber            : String? ,
    @SerializedName("product_sold_to"         ) var productSoldTo        : String? ,
    @SerializedName("review_status"           ) var reviewStatus         : String? ,
    @SerializedName("bid_winner"              ) var bidWinner            : String? ,
    @SerializedName("product_cover_image"     ) var productCoverImage    : String? ,
    @SerializedName("user_image"              ) var userImage            : String? ,
    @SerializedName("receiver_name"           ) var receiverName         : String? ,
    @SerializedName("post_type"               ) var postType             : String? 


):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(productImg)
        parcel.writeString(productName)
        parcel.writeString(productDescription)
        parcel.writeString(productPrice)
        parcel.writeString(firmPriceStatus)
        parcel.writeString(category)
        parcel.writeString(BuyingOption)
        parcel.writeString(conditions)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(sellShipNationStatus)
        parcel.writeString(selectBoxSize)
        parcel.writeString(isDeleted)
        parcel.writeString(createdDtm)
        parcel.writeString(updatedDtm)
        parcel.writeString(categoryId)
        parcel.writeString(userId)
        parcel.writeString(imagePath)
        parcel.writeString(productCoverImg)
        parcel.writeString(distance)
        parcel.writeString(favStatus)
        parcel.writeString(activeSoldStatus)
        parcel.writeString(brandName)
        parcel.writeString(year)
        parcel.writeString(fuelType)
        parcel.writeString(transmissionType)
        parcel.writeString(kmDriven)
        parcel.writeString(mileage)
        parcel.writeString(noOfOwner)
        parcel.writeString(carTitle)
        parcel.writeString(carAdditionalInfo)
        parcel.writeString(bidStartTime)
        parcel.writeString(bidEndTime)
        parcel.writeString(vinNumber)
        parcel.writeString(productSoldTo)
        parcel.writeString(reviewStatus)
        parcel.writeString(bidWinner)
        parcel.writeString(productCoverImage)
        parcel.writeString(userImage)
        parcel.writeString(receiverName)
        parcel.writeString(postType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostDetail> {
        override fun createFromParcel(parcel: Parcel): PostDetail {
            return PostDetail(parcel)
        }

        override fun newArray(size: Int): Array<PostDetail?> {
            return arrayOfNulls(size)
        }
    }

}

class UsersStatus(
    @SerializedName("my_status"              ) var myStatus               : String? ,
    @SerializedName("receiver_status"        ) var receiverStatus         : String? ,
    @SerializedName("user_time"              ) var userTime               : String? ,
    @SerializedName("local_time"             ) var localTime              : String? ,
    @SerializedName("remote_timing"          ) var remoteTiming           : String? ,
    @SerializedName("userprofileavgrating"   ) var userprofileavgrating   : String? ,
    @SerializedName("userprofileratingcount" ) var userprofileratingcount : String? 

):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(myStatus)
        parcel.writeString(receiverStatus)
        parcel.writeString(userTime)
        parcel.writeString(localTime)
        parcel.writeString(remoteTiming)
        parcel.writeString(userprofileavgrating)
        parcel.writeString(userprofileratingcount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UsersStatus> {
        override fun createFromParcel(parcel: Parcel): UsersStatus {
            return UsersStatus(parcel)
        }

        override fun newArray(size: Int): Array<UsersStatus?> {
            return arrayOfNulls(size)
        }
    }

}