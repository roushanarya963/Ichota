package com.ichota.repositories

import com.ichota.model.ReportResModel
import com.ichota.network.RetrofitService

class ReportRepository(private val apiService: RetrofitService) : BaseRepository() {


    suspend fun reportPost(
        userId: String,
        serviceId:String,
        productId:String,
        reportType: String,
        comments: String,
        status: String
    ): ReportResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.reportPostAsync(
                        userId,
                        serviceId,
                        productId,
                        reportType,
                        comments,
                        status
                    ).await()
                },
                error = "Error form server"
            )
        } catch (e: Exception) {
            null
        }

    }


    suspend fun reportUser(
        userId: String,
        reportUserId: String,
        comments: String,
        reportType: String
    ): ReportResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.reportUserProfileAsync(
                        userId,
                        reportUserId,
                        comments,
                        reportType
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }
}