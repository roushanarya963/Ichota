package com.ichota.repositories

import com.ichota.model.UserReviewResModel
import com.ichota.network.RetrofitService

    class TotalRatingItemRepository(private val apiService: RetrofitService) : BaseRepository() {

        suspend fun getTotalRatingItem(userId: String): UserReviewResModel? {
            return try {
                safeApiCall(
                    call = { apiService.totalRatingItemsAsync(userId).await() },
                    error = "Error from server"
                )
            } catch (e: Exception) {
                null
            }

        }
    }

