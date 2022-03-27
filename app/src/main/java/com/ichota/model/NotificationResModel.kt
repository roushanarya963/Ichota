package com.ichota.model

import com.google.gson.annotations.SerializedName

data class NotificationResModel(

   /* @SerializedName("success")
    val success: Int,
    @SerializedName("message_text")
    val message: String,
    @SerializedName("data")
    val data: ArrayList<Notification>*/

    @SerializedName("success"      ) var success     : Int,
    @SerializedName("message_text" ) var messageText : String,
    @SerializedName("data"         ) var data        : ArrayList<Notification> 


)




data class Notification(
   /* @SerializedName("user_id")
    val userId: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("user_image")
    val userImage: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("createdDate")
    val createdDate: String,
    @SerializedName("is_notification")
    val isNotification: String*/

    @SerializedName("id"                  ) var id                : String,
    @SerializedName("user_id"             ) var userId            : String,
    @SerializedName("product_id"          ) var productId         : String,
    @SerializedName("type"                ) var type              : String,
    @SerializedName("message"             ) var message           : String,
    @SerializedName("createdAt"           ) var createdAt         : String,
    @SerializedName("read_status"         ) var readStatus        : String,
    @SerializedName("product_cover_image" ) var productCoverImage : String,
    @SerializedName("post_type"           ) var postType          : String,
    @SerializedName("Buying_option"       ) var BuyingOption      : String,
    @SerializedName("product_details"     ) var productDetails    : ArrayList<ProductDetails>
)
 data class ProductDetails(
     @SerializedName("id"                      ) var id                   : String,
     @SerializedName("product_img"             ) var productImg           : String,
     @SerializedName("product_name"            ) var productName          : String,
     @SerializedName("product_description"     ) var productDescription   : String,
     @SerializedName("product_price"           ) var productPrice         : String,
     @SerializedName("firm_price_status"       ) var firmPriceStatus      : String,
     @SerializedName("category"                ) var category             : String,
     @SerializedName("Buying_option"           ) var BuyingOption         : String,
     @SerializedName("conditions"              ) var conditions           : String,
     @SerializedName("latitude"                ) var latitude             : String,
     @SerializedName("longitude"               ) var longitude            : String,
     @SerializedName("sell_ship_nation_status" ) var sellShipNationStatus : String,
     @SerializedName("select_box_size"         ) var selectBoxSize        : String,
     @SerializedName("isDeleted"               ) var isDeleted            : String,
     @SerializedName("createdDtm"              ) var createdDtm           : String,
     @SerializedName("updatedDtm"              ) var updatedDtm           : String,
     @SerializedName("category_id"             ) var categoryId           : String,
     @SerializedName("user_id"                 ) var userId               : String,
     @SerializedName("image_path"              ) var imagePath            : String,
     @SerializedName("product_cover_img"       ) var productCoverImg      : String,
     @SerializedName("distance"                ) var distance             : String,
     @SerializedName("fav_status"              ) var favStatus            : String,
     @SerializedName("active_sold_status"      ) var activeSoldStatus     : String,
     @SerializedName("brand_name"              ) var brandName            : String,
     @SerializedName("year"                    ) var year                 : String,
     @SerializedName("fuel_type"               ) var fuelType             : String,
     @SerializedName("transmission_type"       ) var transmissionType     : String,
     @SerializedName("km_driven"               ) var kmDriven             : String,
     @SerializedName("mileage"                 ) var mileage              : String,
     @SerializedName("no_of_owner"             ) var noOfOwner            : String,
     @SerializedName("car_title"               ) var carTitle             : String,
     @SerializedName("car_additional_info"     ) var carAdditionalInfo    : String,
     @SerializedName("bid_start_time"          ) var bidStartTime         : String,
     @SerializedName("bid_end_time"            ) var bidEndTime           : String,
     @SerializedName("vin_number"              ) var vinNumber            : String,
     @SerializedName("product_sold_to"         ) var productSoldTo        : String,
     @SerializedName("review_status"           ) var reviewStatus         : String,
     @SerializedName("bid_winner"              ) var bidWinner            : String
 )