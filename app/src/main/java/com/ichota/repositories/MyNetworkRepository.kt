package com.ichota.repositories

import com.ichota.model.FollowerStatusResModel
import com.ichota.model.FollowersResModel
import com.ichota.model.GlobalResModel
import com.ichota.network.RetrofitService

class MyNetworkRepository(private val apiService: RetrofitService) : BaseRepository() {


    suspend fun getUserFollowers(userId: String): FollowersResModel? {

        return try {
            safeApiCall(
                call = { apiService.getUserFollowersAsync(userId).await() },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserFollowings(userId: String): FollowersResModel? {
        return try {
            safeApiCall(
                call = { apiService.getUserFollowingsAsync(userId).await() },
                error = "Error from Server"
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun removeFollower(
        userId: String,
        followerId: String
    ): GlobalResModel? {

        return try {
            safeApiCall(
                call = { apiService.removeFollowerAsync(userId, followerId).await() },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }


    }


    suspend fun unFollowUser(
        userId: String,
        followingId: String
    ): GlobalResModel? {

        return try {
            safeApiCall(
                call = { apiService.unFollowUserAsync(userId, followingId).await() },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }


    }

    suspend fun checkFollowerStatus(
        userId: String,
        profileId: String
    ): FollowerStatusResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.checkFollowerStatusAsync(
                        userId, profileId
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }

    suspend fun toggleFollowUnFollow(
        userId: String,
        followingTo: String
    ): GlobalResModel? {

        return try {
            safeApiCall(
                call = { apiService.toggleFollowUnfollowAsync(userId, followingTo).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }


    }


}