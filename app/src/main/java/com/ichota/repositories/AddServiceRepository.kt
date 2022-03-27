package com.ichota.repositories

import com.ichota.model.AddPostResModel
import com.ichota.model.UploadPostModel
import com.ichota.network.RetrofitService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class AddServiceRepository(private val apiService: RetrofitService) : BaseRepository() {
    suspend fun addService(post: UploadPostModel): AddPostResModel? {

        return try {
            safeApiCall(
                call = {
                    apiService.addServiceAsync(
                        post.userId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.productName.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.productDescription.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.productPrice.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.category.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.categoryId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.latitude.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.longitude.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        post.productImg,
                        post.productCoverImage

                    ).await()
                },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }

    }
}