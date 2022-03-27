package com.ichota.repositories

import com.ichota.model.AddBidResModel
import com.ichota.model.PostDetailResModel
import com.ichota.model.ServicePostDetailResModel
import com.ichota.network.RetrofitService

class PostDetailRepository(private val apiService: RetrofitService) : BaseRepository() {

    suspend fun getPostDetail(userId : String,postId: String): PostDetailResModel? {
        return try {
            safeApiCall(
                call = { apiService.getPostDetailAsync(userId,postId).await() },
                error = "Error for Server"
            )
        } catch (e: Exception) {
            null
        }


    }

    suspend fun getServiceDetail(
        userId : String,
        serviceId: String): ServicePostDetailResModel? {
        return try {
            safeApiCall(
                call = { apiService.getServiceDetailAsync(userId,serviceId).await() },
                error = "Error for Server"
            )
        } catch (e: Exception) {
            null
        }


    }


    suspend fun addToBid(
        userId: String,
        postId: String,
        bidAmount: String
    ): AddBidResModel? {

        return try {
            safeApiCall(
                call = { apiService.addToBidAsync(userId, postId, bidAmount).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }

    }


}