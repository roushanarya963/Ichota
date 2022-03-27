package com.ichota.repositories

import com.ichota.model.GlobalResModel
import com.ichota.model.SafetyBannersResModel
import com.ichota.network.RetrofitService

class SafetyCenterRepository(private val apiService:RetrofitService) : BaseRepository() {

    suspend fun getSafetyBanners(userId:String) : SafetyBannersResModel?{


       return try {

            safeApiCall(
                call = {apiService.getSafetyBannersAsync(userId).await()},
                error = "Error from server"
            )

       }catch (e:Exception){
            null

        }

    }

    suspend fun reportSafetyIssue(
        userId : String,
        reportUserId:String,
        productId:String,
        serviceId:String,
        comments:String
    ): GlobalResModel?{
        return try {
            safeApiCall(call = {apiService.reportSafetyIssueAsync(
                userId,reportUserId,productId,serviceId,comments
            ).await()},
                error = "Error from server")

        }catch (e: Exception){
            null
        }
    }

}