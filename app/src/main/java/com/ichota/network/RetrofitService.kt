package com.ichota.network


import com.google.android.exoplayer2.text.span.TextAnnotation
import com.ichota.model.*
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {

    @FormUrlEncoded
    @POST("register")
    fun registerUserAsync(
        @Field("email") email: String,
        @Field("profile_id") profile_id: String,
        @Field("name") name: String,
        @Field("mobile") mobile: String,
        @Field("password") password: String,
        @Field("device_type") deviceType: String,
        @Field("platform") platform: String,
        @Field("device_token") deviceToken: String
    ): Deferred<Response<LoginResponse>>

    @FormUrlEncoded
    @POST("mark_as_available")
    fun markAsAvailable(
        @Field("id") productId:String
    ):Deferred<Response<GlobalResModel>>

    @FormUrlEncoded
    @POST("getUnseenNotificationCount")
    fun getUnseenNotification(
        @Field("user_id") userId:String
    ):Deferred<Response<TotalUnseenNotificationResModel>>

    @FormUrlEncoded
    @POST("unread_chat_message")
    fun unReadChatMessage(
        @Field("user_id") userId: String
    ):Deferred<Response<UnReadChatMessageResModel>>


    @FormUrlEncoded
    @POST("social_register")
    fun socialRegisterAsync(
        @Field("email") email: String,
        @Field("profile_id") profileId: String,
        @Field("device_token") deviceToken: String,
        @Field("name") name: String,
        @Field("mobile") mobile: String,
        @Field("facebook_id") facebookId: String,
        @Field("gmail_id") gmailId: String,
        @Field("device_type") deviceType: String,
        @Field("platform") platform: String,
        @Field("user_image ") userImg: String,
        @Field("apple_id") appleId:String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Deferred<Response<LoginResponse>>

    @FormUrlEncoded
    @POST("loginuser")
    fun loginUserAsync(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_id") deviceId: String,
        @Field("device_token") deviceToken: String
    ): Deferred<Response<LoginResponse>>

    @FormUrlEncoded
    @POST("forgot_password")
    fun forgotPasswordAsync(
        @Field("email") email: String?
    ): Deferred<Response<ForgotPasswordRes>>


    @GET("category_list")
    fun getSaleCategoriesAsync(): Deferred<Response<SaleResModel>>


    @GET("service_category_list")
    fun getServiceCategoriesAsync(): Deferred<Response<ServiceResModel>>

    @FormUrlEncoded
    @POST("user_view_postby_categoryid")
    fun getSalePostsAsync(
        @Field("category_id") categoryId: String
    ): Deferred<Response<SalePostsResModel>>


    @FormUrlEncoded
    @POST("user_view_serviceby_servicecatid")
    fun getServicePostsAsync(
        @Field("service_categoryid")
        serviceCategoryId: String
    ): Deferred<Response<ServicePostsResModel>>

    @FormUrlEncoded
    @POST("getPostDetails")
    fun getPostDetailAsync(
        @Field("user_id") userId: String,
        @Field("id") postId: String
    ): Deferred<Response<PostDetailResModel>>


    @FormUrlEncoded
    @POST("getServiceDetails")
    fun getServiceDetailAsync(
        @Field("user_id") userId: String,
        @Field("service_id") serviceId: String
    ): Deferred<Response<ServicePostDetailResModel>>


    @FormUrlEncoded
    @POST("add_to_favourite")
    fun addToFavAsync(
        @Field("user_id") userId: String,
        @Field("product_id") postId: String
    ): Deferred<Response<FavouriteResModel>>

  //  https://ichotaa.appdeft.biz/Api/add_to_favourite_service


    @FormUrlEncoded
    @POST("add_to_favourite_service")
    fun addToFavServiceAsync(
        @Field("user_id") userId: String,
        @Field("service_id") postId: String
    ): Deferred<Response<FavouriteResModel>>



    //product_id
    //service_id
    @FormUrlEncoded
    @POST("add_report")
    fun reportPostAsync(
        @Field("user_id") userId: String,
        @Field("service_id")serviceId : String,
        @Field("product_id")productId : String,
        @Field("report_type") reportType: String,
        @Field("comments") comments: String,
        @Field("status") status: String
    ): Deferred<Response<ReportResModel>>


    @FormUrlEncoded
    @POST("add_reportuserprofile")
    fun reportUserProfileAsync(
        @Field("user_id") userId: String,
        @Field("report_user_id") reportUserId: String,
        @Field("comments") comments: String,
        @Field("report_type") reportType: String,
        @Field("status") status: String = "1"

    ): Deferred<Response<ReportResModel>>

    @FormUrlEncoded
    @POST("view_notification")
    fun getNotificationsAsync(
        @Field("user_id") userId: String
    ): Deferred<Response<NotificationResModel>>

    @FormUrlEncoded
    @POST("view_favourite")
    fun getFavouritesDataAsync(
        @Field("user_id") userId: String
    ): Deferred<Response<FavouriteItemResModel>>


    @FormUrlEncoded
    @POST("getPostsOfUser")
    fun getMarketPlacePostAsync(
        @Field("user_id") userId: String
    ): Deferred<Response<MarketPlaceResModel>>

    @FormUrlEncoded
    @POST("getUserDetails")
    fun getUserProfileAsync(
        @Field("user_id") userId: String
    ): Deferred<Response<LoginResponse>>


    @Multipart
    //@POST("products/create")
    @POST("user_add_item_post")
    fun addPostAsync(
        @Part("user_id") userId: RequestBody,
        @Part product_img: ArrayList<MultipartBody.Part?>,
        @Part product_cover_image: MultipartBody.Part?,
        @Part("product_name") productName: RequestBody,
        @Part("product_description") productDescription: RequestBody,
        @Part("product_price") productPrice: RequestBody,
        @Part("firm_price_status") firmPriceStatus: RequestBody,
        @Part("category") category: RequestBody,
        @Part("Buying_option") buyingOptions: RequestBody,
        @Part("conditions") conditions: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("sell_ship_nation_status") sellShipNation: RequestBody,
        @Part("select_box_size") boxSize: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part("brand_name") brandName: RequestBody,
        @Part("year") year: RequestBody,
        @Part("fuel_type") fuelType: RequestBody,
        @Part("transmission_type") transMissionType: RequestBody,
        @Part("km_driven") kmDriven: RequestBody,
        @Part("no_of_owner") noOfOwners: RequestBody,
        @Part("car_title") carTitle: RequestBody,
        @Part("car_additional_info") carAdditionalInfo: RequestBody,
        @Part("bid_start_time")bidStartTime : RequestBody,
        @Part("bid_end_time")bidEndTime : RequestBody,
        @Part("vin_number")vinNumber : RequestBody
    ): Deferred<Response<AddPostResModel>>



    @Multipart
    @POST("user_service_item_post")
    fun addServiceAsync(
        @Part("user_id") userId: RequestBody,
        @Part("service_title") serviceTitle: RequestBody,
        @Part("service_description") serviceDescription: RequestBody,
        @Part("service_min_price") servicePrice: RequestBody,
        @Part("service_category_name") categoryName: RequestBody,
        @Part("service_category_id") categoryId: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part productImg: ArrayList<MultipartBody.Part?>,
        @Part productCoverImg: MultipartBody.Part?,

        ): Deferred<Response<AddPostResModel>>


    @FormUrlEncoded
    @POST("sold_ads")
    fun getSoldAdsAsync(
        @Field("user_id") userId: String
    ): Deferred<Response<SoldAdsResModel>>

    @FormUrlEncoded
    @POST("active_ads")
    fun getActiveAdsAsync(
        @Field("user_id") userId: String
    ): Deferred<Response<ActiveAdsResModel>>


    @FormUrlEncoded
        @POST("search_api_categorey")
    fun getSearchPostAsync(
        @Field("latitude") latitude:String,
        @Field("longitude") longitude:String,
        @Field("Distance") distance:Float,
        @Field("searchtext") searchText: String

    ): Deferred<Response<SearchResModel>>

    /*  @Field("lat") latitude: String,
        @Field("long") longitude: String,
        @Field("miles") miles: String*/

    @FormUrlEncoded
    @POST("fetch_user_chat_history")
    fun getRecentChatDialogsAsync(
        @Field("user_id") userId: String
    ): Deferred<Response<RecentChatDialogsResModel>>

    @FormUrlEncoded
    @POST("getfollowedUsersByMe")
    fun getUserFollowingsAsync(
        @Field("user_id") userId: String
    ): Deferred<Response<FollowersResModel>>


    @FormUrlEncoded
    @POST("getUsersfollowingMe")
    fun getUserFollowersAsync(
        @Field("user_id") userId: String
    ): Deferred<Response<FollowersResModel>>


    @FormUrlEncoded
    @POST("removeUsersIAmfollowing")
    fun unFollowUserAsync(
        @Field("user_id") userId: String,
        @Field("following_id") followingId: String
    ): Deferred<Response<GlobalResModel>>

    @FormUrlEncoded
    @POST("removeMyFollower")
    fun removeFollowerAsync(
        @Field("user_id") userId: String,
        @Field("follower_id") followerId: String
    ): Deferred<Response<GlobalResModel>>


    @FormUrlEncoded
    @POST("check_follower_status")
    fun checkFollowerStatusAsync(
        @Field("user_id") userId: String,
        @Field("profile_id") profileId: String
    ): Deferred<Response<FollowerStatusResModel>>


    @FormUrlEncoded
    @POST("followUnfollowAUser")
    fun toggleFollowUnfollowAsync(
        @Field("user_id") userId: String,
        @Field("following_to") followerId: String,
    ): Deferred<Response<GlobalResModel>>


    @FormUrlEncoded
    @POST("add_to_bid")
    fun addToBidAsync(
        @Field("user_id") userId: String,
        @Field("product_id") postId: String,
        @Field("bidamount") bidAmount: String
    ): Deferred<Response<AddBidResModel>>


    @Multipart
    @POST("update_profileimage")
    fun updateProfileImagesAsync(
        @Part profileImage: MultipartBody.Part?,
        @Part("user_id") userId: RequestBody
    ): Deferred<Response<ProfileImageResModel>>


    @FormUrlEncoded
    @POST("update_emergency_contact")
    fun updateEmergencyContactAsync(
        @Field("user_id") userId: String,
        @Field("emergency_contact") contact: String


    ): Deferred<Response<UpdateEmergencyContactModel>>


    @Multipart
    @POST("insert_chat")
    fun insertChatAsync(
        @Part("user_id") userId: RequestBody,
        @Part("receiver_id") receiverId: RequestBody,
        @Part("read_status") readStatus: RequestBody,
        @Part("message") message: RequestBody,
        @Part("product_id") productId: RequestBody,
        @Part("post_type") postType:RequestBody,
        @Part("msg_type") messageType: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part image: MultipartBody.Part?,

    ): Deferred<Response<InsertChatResModel>>

    @FormUrlEncoded
    @POST("open_chat")
    fun readChatAsync(
        @Field("user_id") userId: String,
        @Field("product_id") productId: String,
        @Field("receiver_id") receiverId: String,
        @Field("post_type") postType:String,
    ): Deferred<Response<GlobalResModel>>


    @FormUrlEncoded
    @POST("view_chat")
    fun viewChatAsync(
        @Field("user_id") userId: String,
        @Field("receiver_id") receiverId: String,
        @Field("product_id") productId: String,
        @Field("post_type") postType:String
    ): Deferred<Response<ViewChatResModel>>

    @FormUrlEncoded
    @POST("mark_allread")
    fun markAllAsReadAsync(
        @Field("user_id") userId: String
    ): Deferred<Response<MarkAllAsReadResModel>>


    @FormUrlEncoded
    @POST("verify_email")
    fun verifyEmailAsync(
        @Field("email") email: String
    ): Deferred<Response<VerifyEmailResModel>>


    @FormUrlEncoded
    @POST("verifyMobile")
    fun verifyMobileNumberAsync(
        @Field("mobile") mobile: String
    ):Deferred<Response<VerifyMobileResModel>>


    @FormUrlEncoded
    @POST("updateSocial")
    fun updateSocialAsync(
        @Field("user_id") userId: String,
        @Field("f_id") facebookId: String,
        @Field("g_id") googleId: String
    ): Deferred<Response<LoginResponse>>


    @FormUrlEncoded
    @POST("mark_as_sold")
    fun markAsSoldAsync(
        @Field("id") postId: String,
        @Field("user_id") userId: String
    ): Deferred<Response<GlobalResModel>>

    
    @FormUrlEncoded
    @POST("mark_and_sold")
    fun markAndSold(
        @Field("user_id") userId:String,
        @Field("product_id") productId:String
    ): Deferred<Response<MarkAndSoldResModel>>

    @FormUrlEncoded
    @POST("mark_as_deleted")
    fun markAsDeletedAsync(
        @Field("id") postId: String,
        @Field("user_id") userId: String
    ): Deferred<Response<GlobalResModel>>


    @FormUrlEncoded
    @POST("changePassword")
    fun changePasswordAsync(
        @Field("user_id") userId: String,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String
    ): Deferred<Response<GlobalResModel>>

    @FormUrlEncoded
    @POST("changeOnlineOfflineStatus")
    fun changeUserActiveStatusAsync(
        @Field("user_id") userId: String,
        @Field("status") status: String
    ): Deferred<Response<GlobalResModel>>



    @Multipart
    @POST("saveChatImages")
    fun saveChatImagesAsync(
        @Part("sender_id") senderId: RequestBody,
        @Part("receiver_id") receiverId: RequestBody,
        @Part("read_status") readStatus: RequestBody,
        @Part("message_type") messageType: RequestBody,
        @Part("product_id") productId: RequestBody,
        @Part image: MultipartBody.Part
    ): Deferred<Response<GlobalResModel>>


    @FormUrlEncoded
    @POST("getaddress")
    fun getAddressFromZipCodeAsync(
        @Field("zip") zipCode: String
    ): Deferred<Response<GetAddressResModel>>

    @FormUrlEncoded
    @POST("update_user_details")
    fun updateUserDetailsAsync(
        @FieldMap requestMap: HashMap<String, String>
    ): Deferred<Response<UpdateUserDetailResModel>>

    @FormUrlEncoded
    @POST("update_user_details")
    fun sendOptToEmailAsync(
        @FieldMap requestMap: HashMap<String, String>
    ): Deferred<Response<GlobalResModel>>

    @FormUrlEncoded
    @POST("getPaymentDetails")
    fun getPaymentMethodsAsync(
        @Field("user_id")userId:String
    ): Deferred<Response<PaymentMethodsResModel>>

    @FormUrlEncoded
    @POST("update_user_payment")
    fun updatePaymentMethodUrlAsync(
        @Field("user_id")userId:String,
        @Field("payment_status")paymentStatus:String,
        @Field("payment_id")paymentId:String,
        @Field("user_url")userUrl:String,
    ):Deferred<Response<GlobalResModel>>


    @FormUrlEncoded
    @POST("search_with_filter")
    fun searchSalePostWithFilterAsync(
        @Field("minimum_product_price")minimumProductPrice : String,
        @Field("maximum_product_price")maximumProductPrice : String,
        @Field("Buying_option")BuyingOptions : String,
        @Field("conditions")conditions : String,
        @Field("Distance")Distance : String,
        @Field("newest")newest : String,
        @Field("hightolow")hightolow : String,
        @Field("lowtohigh")lowtohigh : String,
        @Field("latitude")latitude : String,
        @Field("longitude")longitude : String,
        @Field("cat_id")catId : String,
        ) : Deferred<Response<SalePostsResModel>>

    @FormUrlEncoded
    @POST("search_with_filter")
    fun searchServicePostWithFilterAsync(
        @Field("minimum_product_price")minimumProductPrice : String,
        @Field("maximum_product_price")maximumProductPrice : String,
        @Field("Buying_option")BuyingOptions : String,
        @Field("conditions")conditions : String,
        @Field("Distance")Distance : String,
        @Field("newest")newest : String,
        @Field("hightolow")hightolow : String,
        @Field("lowtohigh")lowtohigh : String,
        @Field("latitude")latitude : String,
        @Field("longitude")longitude : String,
        @Field("cat_id")catId : String,
    ) : Deferred<Response<ServicePostsResModel>>




 @FormUrlEncoded
 @POST("getSafetyBanner")
 fun getSafetyBannersAsync(
     @Field("user_id")userId:String
 ) : Deferred<Response<SafetyBannersResModel>>


 @FormUrlEncoded
 @POST("getServicesOfUser")
 fun getUserServicePostsAsync(
     @Field("user_id")userId:String
 ):Deferred<Response<ServicePostsResModel>>

    @FormUrlEncoded
    @POST("getPostsOfUser")
    fun getUserSalePostsAsync(
        @Field("user_id")userId:String
    ):Deferred<Response<SalePostsResModel>>


    @FormUrlEncoded
    @POST("sendSafetyReports")
    fun reportSafetyIssueAsync(
        @Field("user_id")userId: String,
        @Field("report_user_id")reportUserId: String,
        @Field("product_id")productId: String,
        @Field("service_id")serviceId: String,
        @Field("comments")comment:String
    ):Deferred<Response<GlobalResModel>>

    @FormUrlEncoded
    @POST("total_rating_items")
    fun totalRatingItemsAsync(
        @Field("user_id")userId: String
    ):Deferred<Response<UserReviewResModel>>


}