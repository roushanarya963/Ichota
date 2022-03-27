package com.ichota.repositories

import com.ichota.model.ActiveAdsResModel
import com.ichota.model.GlobalResModel
import com.ichota.model.MarkAndSoldResModel
import com.ichota.model.SoldAdsResModel
import com.ichota.network.RetrofitService

class MyListingRepository(private val apiService: RetrofitService) : BaseRepository() {


    suspend fun getSoldAds(userId: String): SoldAdsResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.getSoldAdsAsync(userId).await()
                },
                error = "Error from Server"
            )

        } catch (e: Exception) {
            null
        }
    }

    suspend fun getActiveAds(userId: String): ActiveAdsResModel? {
        return try {
            safeApiCall(
                call = { apiService.getActiveAdsAsync(userId).await() },
                error = "Error form server"
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun markAsSold(
        postId : String,
        userId : String
    ) : GlobalResModel?{
        return try {
            safeApiCall(call = {apiService.markAsSoldAsync(postId,userId).await()},
            error = "Error from server")

        }catch (e: Exception){
            null
        }

    }


    suspend fun markAndSold(
        userId:String,
        productId:String
    ) : MarkAndSoldResModel?{
        return try{
            safeApiCall(call = {apiService.markAndSold(userId,productId).await()},
               error = "Error from server"
            )
        }catch (e : Exception){
            null
        }
    }



    suspend fun markAsDeleted(
        postId : String,
        userId : String
    ):GlobalResModel?{
        return try {
            safeApiCall(call = {apiService.markAsDeletedAsync(postId,userId).await()},
            error = "Error from server")

        }catch (e: Exception){
            null
        }
    }



}