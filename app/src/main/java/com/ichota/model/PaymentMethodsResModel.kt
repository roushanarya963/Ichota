package com.ichota.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PaymentMethodsResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message : String,
    @SerializedName("data")
    val paymentMethods : List<PaymentMethod>

)


data class PaymentMethod(
    @SerializedName("id")
    val id : String,
    @SerializedName("title")
    val title : String,
    @SerializedName("image")
    val image: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("payment_cover_img")
    val productCoverImage : String,
    @SerializedName("user_url")
    val userUrl:String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(status)
        parcel.writeString(productCoverImage)
        parcel.writeString(userUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentMethod> {
        override fun createFromParcel(parcel: Parcel): PaymentMethod {
            return PaymentMethod(parcel)
        }

        override fun newArray(size: Int): Array<PaymentMethod?> {
            return arrayOfNulls(size)
        }
    }
}