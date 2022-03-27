package com.ichota.repositories

import android.util.Log
import com.ichota.model.AddPostResModel
import com.ichota.model.UploadPostModel
import com.ichota.network.RetrofitService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class AddPostRepository(private val apiService: RetrofitService) : BaseRepository() {


    suspend fun addPost(post: UploadPostModel): AddPostResModel? {

        Log.d("TAG", "addPost: ${post.productImg}")

        return try {
            safeApiCall(
                call = {
                    apiService.addPostAsync(
                        post.userId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.productImg,
                        post.productCoverImage,
                        post.productName.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.productDescription.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.productPrice.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.firmOnPrice.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.category.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.buying_option.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.condition.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.latitude.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.longitude.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.sellShipNationStatus.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.boxSize.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.categoryId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.brandName.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.year.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.fuelType.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.transmissionType.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.kmDriven.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.noOfOwners.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.carTitle.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.cardAdditionalInfo.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.bidStartTime.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.bidEndTime .toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.vinNumber .toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                    ).await()
                },
                error = "Error from server"
            )
        } catch (e: Exception) {
            Log.e("TAG", "addPost: exception = ${e}", )
            null
        }

    }




}