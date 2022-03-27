package com.ichota.repositories

import com.ichota.model.*
import com.ichota.network.RetrofitService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Part

class ChatRepository(private val apiService: RetrofitService) : BaseRepository() {

    suspend fun getRecentChatDialogs(userId: String): RecentChatDialogsResModel? {
        return try {
            safeApiCall(
                call = { apiService.getRecentChatDialogsAsync(userId).await() },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }
    }
// insert chat
    suspend fun insertChat(
        userId: String,
        receiverId: String,
        readStatus: String = "0",
        message: String,
        productId: String,
        postType:String,
        messageType: String,
        lat: String,
        lng: String,
        image : MultipartBody.Part?= null,
    ): InsertChatResModel? {

        return try {
            safeApiCall(
                call = {
                    apiService.insertChatAsync(
                        userId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        receiverId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        readStatus.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        message.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        productId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        postType.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        messageType.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        lat.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        lng.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        image,
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }

    }
// open chat
    suspend fun readChat(
        userId: String,
        productId: String,
        receiverId: String,
        postType: String

    ): GlobalResModel? {
        return try {
            safeApiCall(
                call = { apiService.readChatAsync(userId, productId,receiverId,postType).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }
    // view chat
    suspend fun viewChat(
        userId: String,
        receiverId: String,
        productId: String,
        postType:String,
    ): ViewChatResModel? {
        return try {
            safeApiCall(
                call = { apiService.viewChatAsync(userId, receiverId,productId, postType).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }

    suspend fun markAllAsRead(
        userId: String
    ): MarkAllAsReadResModel? {
        return try {
            safeApiCall(
                call = { apiService.markAllAsReadAsync(userId).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }



    suspend fun changeUserActiveStatus(
        userId: String,
        status: String

    ): GlobalResModel? {

        return try {
            safeApiCall(call = {apiService.changeUserActiveStatusAsync(userId,status).await()},
            error = "error from server")

        } catch (e: Exception) {
            null
        }

    }

    suspend fun saveChatImage(
        senderId : String,
        receiverId : String,
        productId : String,
        image : MultipartBody.Part,
        readStatus : String = "0",
        messageType : String = MessageType.TYPE_IMAGE,

    ) : GlobalResModel?{
        return try {
           safeApiCall(call = {apiService.saveChatImagesAsync(
               senderId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
               receiverId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
               readStatus.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
               messageType.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
               productId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
               image

           ).await()},
           error = "Error from server")
        }catch (e:Exception){
            null
        }

    }
}