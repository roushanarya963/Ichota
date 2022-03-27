package com.ichota.repositories

import com.ichota.model.SalePostsResModel
import com.ichota.model.SaleResModel
import com.ichota.network.RetrofitService

class SaleRepository(private val apiService: RetrofitService) : BaseRepository() {


    suspend fun getSaleCategories(): SaleResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.getSaleCategoriesAsync().await()
                },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSalePosts(
        categoryId: String
    ): SalePostsResModel? {
        return try {
            safeApiCall(
                call = { apiService.getSalePostsAsync(categoryId).await() },
                error = "Error from Server"
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchSalePostWithFilter(
        minimumProductPrice: String,
        maximumProductPrice: String,
        buyingOption:String,
        conditions:String,
        newest:String,
        highToLow:String,
        lowToHigh:String,
        latitude:String,
        longitude:String,
        categoryId:String,
        distance:String ="5"
    ) : SalePostsResModel?{
        return try {
            safeApiCall(
                call = {apiService.searchSalePostWithFilterAsync(
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