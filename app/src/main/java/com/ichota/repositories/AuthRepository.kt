package com.ichota.repositories

import android.util.Log
import com.ichota.model.*
import com.ichota.network.RetrofitService

class AuthRepository(private val apiService: RetrofitService) : BaseRepository() {



    suspend fun registerUser(
        name: String,
        email: String,
        deviceId: String,
        mobile: String,
        password: String,
        deviceToken : String
    ): LoginResponse? {

        Log.d("TAG", "registerUser: deviceToken = $deviceToken")
        return try {
            safeApiCall(
                call = {
                    apiService.registerUserAsync(
                        email,
                        deviceId,
                        name,
                        mobile,
                        password,
                        "ANDROID",
                        "Mobile",
                        deviceToken
                    ).await()
                },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }

    }


    suspend fun login(
        email: String,
        password: String,
        deviceId: String,
        deviceToken: String
    ): LoginResponse? {
        return try {
            safeApiCall(
                call = {
                    apiService.loginUserAsync(
                        email,
                        password,
                        deviceId,
                        deviceToken

                    ).await()
                },
                error = "Error from Server"
            )
        } catch (e: Exception) {
            null
        }

    }

    suspend fun forgotPassword(email: String): ForgotPasswordRes? {

        return try {
            safeApiCall(
                call = {
                    apiService.forgotPasswordAsync(email).await()
                },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }
    }


    suspend fun socialLogin(
        email: String,
        profileId: String,
        deviceToken: String,
        name: String,
        mobile: String,
        facebookId: String,
        gmailId: String,

        platform: String,
        userImg: String,
        deviceType: String = "ANDROID",
        appleId:String= "",
        latitude: String = "0",
        longitude: String = "0"
    ): LoginResponse? {
        return try {
            safeApiCall(
                call = {
                    apiService.socialRegisterAsync(
                        email,
                        profileId,
                        deviceToken,
                        name,
                        mobile,
                        facebookId,
                        gmailId,
                        deviceType,
                        platform,
                        userImg,
                        appleId,
                        latitude,
                        longitude).await()
                       },
                error = "Error from server"
            )


        } catch (e: Exception) {
            null
        }

    }


    suspend fun verifyEmail(email: String): VerifyEmailResModel? {
        return try {
            safeApiCall(
                call = { apiService.verifyEmailAsync(email).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }

    }


    suspend fun verifyMobile(mobile:String):VerifyMobileResModel? {
        return try {
            safeApiCall(
                call = {apiService.verifyMobileNumberAsync(mobile).await()},
                error = "Error from server"
            )
        }catch (e : Exception){
            null
        }
    }


    suspend fun updateSocial(
        userId: String,
        facebookId: String,
        googleId: String

    ): LoginResponse? {
        return safeApiCall(
            call = { apiService.updateSocialAsync(userId, facebookId, googleId).await() },
            error = "error from server"
        )

    }


    suspend fun changePassword(
        userId : String,
        oldPassword : String,
        newPassword:String

    ):GlobalResModel?{
        return try {
            safeApiCall(
                call = {apiService.changePasswordAsync(userId,oldPassword,newPassword).await()},
                error = "Error from servicer"
            )

        }catch (e: Exception){
            null
        }
    }

    suspend fun productDetailAvailable(
        id:String
    ):GlobalResModel?{
        return try {
            safeApiCall(
                call =  { apiService.markAsAvailable(id).await() },
                error = "Error from service"
            )
        }catch (e:Exception){
            null
        }
    }

   suspend fun unReadChatMessage(
       userId:String
   ):UnReadChatMessageResModel?{
       return try{
           safeApiCall(
               call = {apiService.unReadChatMessage(userId).await()},
               error = "Error from service"
           )
       }catch (e:Exception){
           null
       }
   }

   suspend fun tOtalUnseenNotification(
       userId:String
   ):TotalUnseenNotificationResModel?{
       return try {
           safeApiCall(
               call = {apiService.getUnseenNotification(userId).await() },
               error = "Error from server"
           )
       }catch (e:Exception){
           Log.d("TAG", "tOtalUnseenNotification: $e")
           null
       }
   }

}