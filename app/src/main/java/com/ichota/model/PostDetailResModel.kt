package com.ichota.model

import com.google.gson.annotations.SerializedName

data class PostDetailResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<Post>

)


data class Post(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("product_img")
    val productImages: ArrayList<String>,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("product_description")
    val productDescription: String?,
    @SerializedName("product_price")
    val productPrice: String?,
    @SerializedName("firm_price_status")
    val firmPriceStatus: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("Buying_option")
    val buyingOptions: String?,
    @SerializedName("conditions")
    val conditions: String?,
    @SerializedName("latitude")
    val latitude: String?,
    @SerializedName("longitude")
    val longitude: String?,
    @SerializedName("sell_ship_nation_status")
    val sellShipNationStatus: String,
    @SerializedName("select_box_size")
    val selectBoxSize: String,
    @SerializedName("isDeleted")
    val isDeleted: String?,
    @SerializedName("createdDtm")
    val postCreatedTime: String,
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("product_cover_image")
    val productCoverImage: String?,
    @SerializedName("distance")
    val distance: String?,
    @SerializedName("userdetail")
    val postedUser: ArrayList<PostedUser>,
    @SerializedName("is_favourite")
    val favStatus: String,
    @SerializedName("active_sold_status")
    val activeSoldStatus: String?,
    @SerializedName("brand_name")
    val brandName : String?,
    @SerializedName("year")
    val year : String?,
    @SerializedName("fuel_type")
    val fuelType : String?,
    @SerializedName("transmission_type")
    val transmissionType : String?,
    @SerializedName("km_driven")
    val kmDriven : String?,
    @SerializedName("no_of_owner")
    val numOfOwner : String?,
    @SerializedName("car_title")
    val carTitle : String?,
    @SerializedName("car_additional_info")
    val carAdditionalInfo : String?,

    @SerializedName("bid_end_time")
    val bidEndTime : String? = null,
    @SerializedName("bid_start_time")
    val bidStartTime :String? =null,

    @SerializedName("bidsdetails")
    val bidDetails: ArrayList<BidDetail>,
    @SerializedName("currentbid")
    val currentBid: Int,
    @SerializedName("minbidamont")
    val minBidAmount: Int,
    @SerializedName("maxbidamount")
    val maxBidAmount: Int
)


data class PostedUser(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("emergency_contact")
    val emergencyContact: String?,
    @SerializedName("roleId")
    val rollId: String,
    @SerializedName("isDeleted")
    val isDeleted: String,
    @SerializedName("createdBy")
    val createdBy: String?,
    @SerializedName("isactive")
    val isActive: String,
    @SerializedName("approve_reject")
    val approveReject: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("platform")
    val platform: String,
    @SerializedName("user_image")
    val userImage: String,
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
    val userCoverImage: String?,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("comment")
    val comments: String?,
    @SerializedName("Email_status")
    val emailStatus: String?,
    @SerializedName("instagram_status")
    val instagramStatus: String?,
    @SerializedName("phone_no_status")
    val phoneNoStatus: String?,
    @SerializedName("facebook_status")
    val facebookStatus: String?,
    @SerializedName("secure_status")
    val secureStatus: String?,
    @SerializedName("paypal_status")
    val paypalStatus: String?,
    @SerializedName("cashapp_status")
    val cashAppStatus: String?,
    @SerializedName("venmo_status")
    val venmoStatus: String,
    @SerializedName("zelle_status")
    val zelleStatus: String,
    @SerializedName("google_play_status")
    val googlePayStatus: String?,
    @SerializedName("linked_card_status")
    val linkedCardStatus: String?,
    @SerializedName("token")
    val token: String?
)


data class BidDetail(

    @SerializedName("user_id")
    val userId : String,
    @SerializedName("name")
    val name: String,
    @SerializedName("user_image")
    val userImage: String,
    @SerializedName("bidamount")
    val bidAmount: String,
    @SerializedName("createdon")
    val createdOn: String


)




