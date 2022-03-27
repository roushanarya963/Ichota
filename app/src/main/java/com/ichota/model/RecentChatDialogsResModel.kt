package com.ichota.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RecentChatDialogsResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String?,
    @SerializedName("data")
    val chatDialogs: ArrayList<ChatDialog>
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        TODO("chats")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(success)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecentChatDialogsResModel> {
        override fun createFromParcel(parcel: Parcel): RecentChatDialogsResModel {
            return RecentChatDialogsResModel(parcel)
        }

        override fun newArray(size: Int): Array<RecentChatDialogsResModel?> {
            return arrayOfNulls(size)
        }
    }
}

data class ChatDialog(
    @SerializedName("post_type"           ) var postType           : String?,
    @SerializedName("id"                  ) var id                : String?,
    @SerializedName("read_status"         ) var readStatus        : String?,
    @SerializedName("msg_type"            ) var msgType           : String?,
    @SerializedName("product_id"          ) var productId         : String?,
    @SerializedName("receiver_id"         ) var receiverId        : String?,
    @SerializedName("user_id"             ) var userId            : String?,
    @SerializedName("message"             ) var message           : String?,
    @SerializedName("createdDtm"          ) var createdDtm        : String?,
    @SerializedName("product_name"        ) var productName       : String?,
    @SerializedName("product_price"       ) var productPrice      : String?,
    @SerializedName("product_cover_img"   ) var productCoverImg   : String?,
    @SerializedName("product_cover_image" ) var productCoverImage : String?,
    @SerializedName("user_image1"         ) var userImage1        : String?,
    @SerializedName("user_name"           ) var userName          : String?,
    @SerializedName("msg_count"           ) var msgCount          : String?

):Parcelable {
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
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }

    companion object CREATOR : Parcelable.Creator<ChatDialog> {
        override fun createFromParcel(parcel: Parcel): ChatDialog {
            return ChatDialog(parcel)
        }

        override fun newArray(size: Int): Array<ChatDialog?> {
            return arrayOfNulls(size)
        }
    }
}


