package com.ichota.repositories

import com.ichota.model.GetAddressResModel
import com.ichota.network.RetrofitService

class LocationRepository(private val apiService : RetrofitService) :BaseRepository() {


    suspend fun getAddressFromZip(zipCode : String):GetAddressResModel?{
        return try {
            safeApiCall(
                call = {apiService.getAddressFromZipCodeAsync(zipCode).await()},
                error = "Error from server"
            )

        } catch (e:Exception){
            null
        }
    }
}