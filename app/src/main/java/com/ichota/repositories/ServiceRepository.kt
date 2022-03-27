package com.ichota.repositories

import com.ichota.model.GlobalResModel
import com.ichota.model.ServicePostsResModel
import com.ichota.model.ServiceResModel
import com.ichota.network.RetrofitService

class ServiceRepository(private val apiService: RetrofitService) : BaseRepository() {

    suspend fun getServiceCategories(): ServiceResModel? {
        return try {
            safeApiCall(
                call = { apiService.getServiceCategoriesAsync().await() },
                error = "Error from Server"
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getServicePosts(
        categoryId: String
    ): ServicePostsResModel? {
        return try {
            safeApiCall(
                call = { apiService.getServicePostsAsync(categoryId).await() },
                error = "Error from Server"
            )
        } catch (e: Exception) {
            null
        }
    }


    suspend fun toggleAvailableUnAvailable(
        id:String
    ): GlobalResModel?{
        return try {
            safeApiCall(
                call =  { apiService.markAsAvailable(id).await() },
                error = "Error from service"
            )
        }catch (e:Exception){
            null
        }
    }

    suspend fun searchServicePostWithFilter(
        minimumProductPrice: String,
        maximumProductPrice: String,
        buyingOption:String,
        conditions:String,
        distance:String,
        newest:String,
        highToLow:String,
        lowToHigh:String,
        latitude:String,
        longitude:String,
        categoryId:String
    ) : ServicePostsResModel?{
        return try {
            safeApiCall(
                call = {apiService.searchServicePostWithFilterAsync(
                    minimumProductPrice,
                    maximumProductPrice,
                    buyingOption,
                    conditions,
                    distance,
                    newest,
                    highToLow,
                    lowToHigh,
                    latitude,
                    longitude,
                    categoryId
                ).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            null
        }
    }
}