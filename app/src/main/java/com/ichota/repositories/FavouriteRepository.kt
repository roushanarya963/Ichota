package com.ichota.repositories

import android.util.Log
import com.ichota.model.FavouriteItemResModel
import com.ichota.model.FavouriteResModel
import com.ichota.network.RetrofitService

class FavouriteRepository(private val apiService: RetrofitService) : BaseRepository() {

    suspend fun getFavouriteData(userId: String): FavouriteItemResModel? {
        return try {
            safeApiCall(
                call = { apiService.getFavouritesDataAsync(userId).await() },
                error = "Error from Server"
            )
        } catch (e: Exception) {
            Log.d("TAG", "addToFavViewFavList:$e ")
            null
        }
    }


    suspend fun addToFav(
        userId: String,
        postId: String
    ): FavouriteResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.addToFavAsync(
                        userId,
                        postId
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "addToFavSale:$e ")
            null
        }

    }


    suspend fun addToFavService(
        userId: String,
        postId: String
    ): FavouriteResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.addToFavServiceAsync(
                        userId,
                        postId
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "addToFavService:$e ")
            null
        }
    }

}