package com.ichota.repositories

import android.util.Log
import com.ichota.model.*
import com.ichota.network.RetrofitService
import com.ichota.utils.Global
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileRepository(private val apiService: RetrofitService) : BaseRepository() {


    suspend fun getMarketPlacePosts(
        userId: String
    ): MarketPlaceResModel? {
        return try {
            safeApiCall(
                call = { apiService.getMarketPlacePostAsync(userId).await() },
                error = "Error form server"
            )
        } catch (e: Exception) {
            null
        }
    }


    suspend fun getUserProfile(
        userId: String
    ): LoginResponse? {
        return try {
            safeApiCall(
                call = { apiService.getUserProfileAsync(userId).await() },
                error = "Error from Server"
            )
        } catch (e: Exception) {
            Log.d("TAG", "getUserProfile: Exception  $e ")
            null
        }


    }

    suspend fun updateProfileImage(
        userId: String,
        profileImage: MultipartBody.Part?
    ): ProfileImageResModel? {
        return try {
            safeApiCall(
                    call = {
                        apiService.updateProfileImagesAsync(
                                profileImage,
                                userId.toRequestBody("multipart/form-data".toMediaTypeOrNull())).await() },
                    error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }

    }


    suspend fun updateEmergencyContact(
        userId: String,
        contact: String
    ): UpdateEmergencyContactModel? {
        return try {
            safeApiCall(
                call = { apiService.updateEmergencyContactAsync(userId, contact).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }

    }

    suspend fun updateUserDetail(
        request : HashMap<String,String>
    ) : UpdateUserDetailResModel?{
        return try {
            safeApiCall(
                call = {apiService.updateUserDetailsAsync(request).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            null
        }
    }

    suspend fun sendOtpToEmail(
        request : HashMap<String,String>
    ) : GlobalResModel?{
        return try {
            safeApiCall(
                call = {apiService.sendOptToEmailAsync(request).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            null
        }
    }


    suspend fun getPaymentMethods(userId:String) : PaymentMethodsResModel?{
        return try {
            safeApiCall(
                call = {apiService.getPaymentMethodsAsync(userId).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            null
        }

    }


    suspend fun updatePaymentMethod(
        userId:String,
        paymentStatus:String,
        paymentId:String,
        paymentUrl:String
    ) : GlobalResModel?{
        return try {
            safeApiCall(
                call = {apiService.updatePaymentMethodUrlAsync(userId,paymentStatus,paymentId,paymentUrl).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            null
        }

    }


    suspend fun getUserServicePosts(userId:String):ServicePostsResModel?{
        return try {
            safeApiCall(
                call = {apiService.getUserServicePostsAsync(userId).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            Log.d("TAG", "getUserServicePosts: exception = ${e.localizedMessage}")
            null
        }
    }


    suspend fun getUserSalePosts(userId:String):SalePostsResModel?{
        return try {
            safeApiCall(
                call = {apiService.getUserSalePostsAsync(userId).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            null
        }
    }




}