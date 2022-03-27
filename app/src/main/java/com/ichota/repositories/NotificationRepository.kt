package com.ichota.repositories

import com.ichota.model.NotificationResModel
import com.ichota.network.RetrofitService

class NotificationRepository(private val apiService: RetrofitService) : BaseRepository() {


    suspend fun getNotifications(userId: String): NotificationResModel? {
        return try {
            safeApiCall(
                call = { apiService.getNotificationsAsync(userId).await() },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }

    }
}