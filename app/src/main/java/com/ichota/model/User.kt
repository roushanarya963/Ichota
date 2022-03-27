package com.ichota.model

import com.google.gson.annotations.SerializedName

data class User(


    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("emergency_contact")
    var emergencyContact: String,

    @SerializedName("roleId")
    val roleId: String,
    @SerializedName("isDeleted")
    val isDeleted: String,
    @SerializedName("createdBy")
    val createdBy: String,
    @SerializedName("createdDtm")
    val createDtm: String,
    @SerializedName("updatedBy")
    val updated: String?,
    @SerializedName("updatedDtm")
    val updatedDtm: String?,
    @SerializedName("isactive")
    val isActive: String,
    @SerializedName("approve_reject")
    val approveReject: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("platform")
    val platform: String,
    @SerializedName("user_image")
    var userImage: String,
    @SerializedName("device_type")
    val deviceType: String,
    @SerializedName("gmail_id")
    val gmailId: String,
    @SerializedName("facebook_id")
    val facebookId: String,
    @SerializedName("Feedback")
    val feedback: String?,
    @SerializedName("otptoken")
    val otpToken: String?,
    @SerializedName("latitude")
    val latitude: String?,
    @SerializedName("longitude")
    val longitude: String?,
    @SerializedName("user_coverimg")
    val userCoverImage: String,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("comment")
    val comments: String,
    @SerializedName("Email_status")
    val emailStatus: String?,
    @SerializedName("instagram_status")
    val instagramStatus: String?,
    @SerializedName("phone_no_status")
    val phoneNumStatus: String?,
    @SerializedName("facebook_status")
    val facebookStatus: String?,
    @SerializedName("secure_status")
    val secureStatus: String?,
    @SerializedName("paypal_status")
    val paypalStatus: String?,
    @SerializedName("cashapp_status")
    val cashAppStatus: String?,
    @SerializedName("venmo_status")
    val venmoStatus: String?,
    @SerializedName("zelle_status")
    val zelleStatus: String?,
    @SerializedName("google_play_status")
    val googlePayStatus: String?,
    @SerializedName("applepay_status")
    val applePayStatus:String,
    @SerializedName("linked_card_status")
    val linkedCardStatus: String?,
    @SerializedName("token")
    val token: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("follower_count")
    var followerCount: String = "",
    @SerializedName("following_count")
    val followingCount: String = ""


)