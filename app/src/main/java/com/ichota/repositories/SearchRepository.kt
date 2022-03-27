package com.ichota.repositories

import com.ichota.model.SalePostsResModel
import com.ichota.model.SearchResModel
import com.ichota.network.RetrofitService

class SearchRepository(private val apiService: RetrofitService) : BaseRepository() {

    suspend fun getSearchData(
        latitude:String,
        longitude: String,
        distance: Float,
        searchText: String
    ): SearchResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.getSearchPostAsync(
                        latitude,
                        longitude,
                        distance,
                        searchText,
                    ).await()
                },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }

    }
}