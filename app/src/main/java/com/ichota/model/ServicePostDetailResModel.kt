package com.ichota.model

import com.google.gson.annotations.SerializedName

data class ServicePostDetailResModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<ServicePostDetail>
)

data class ServicePostDetail(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("service_img")
    val productImages: ArrayList<String>,
    @SerializedName("service_title")
    val productName: String?,
    @SerializedName("service_description")
    val productDescription: String?,
    @SerializedName("service_category_name")
    val category: String?,
    @SerializedName("latitude")
    val latitude: String?,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("isDeleted")
    val isDeleted: String?,
    @SerializedName("createdon")
    val postCreatedTime: String,
    @SerializedName("service_category_id")
    val categoryId: String,
    @SerializedName("service_min_price")
    val serviceMinimumPrice :String,
    @SerializedName("product_cover_image")
    val productCoverImage: String?,
    @SerializedName("distance")
    val distance: String?,
    @SerializedName("userdetail")
    val postedUser: ArrayList<PostedUser>,
    @SerializedName("is_favourite")
    val favStatus: String,
    @SerializedName("mark_as_available")
    val markAsAvailable: String,
    @SerializedName("userprofileavgrating")
    val userprofileavgrating:String,
    @SerializedName("userprofileratingcount")
    val userprofileratingcount: String

    /*@SerializedName("bidsdetails")
    val bidDetails: ArrayList<BidDetail>,
    @SerializedName("currentbid")
    val currentBid: Int,
    @SerializedName("minbidamont")
    val minBidAmount: Int,
    @SerializedName("maxbidamount")
    val maxBidAmount: Int*/

)










