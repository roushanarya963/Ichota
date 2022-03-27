package com.ichota.viewModel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.*
import com.ichota.network.APIFactory
import com.ichota.repositories.AuthRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val TAG = "AuthViewModel";

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val authRepo = AuthRepository(APIFactory.makeServiceAPi())

    private val liveDataMessage = SingleLiveEvent<String>()
    private val liveDataProgress = SingleLiveEvent<Boolean>()
    private val liveDataChangePassword = MutableLiveData<GlobalResModel>()
    private val liveDataRegisterResponse = MutableLiveData<LoginResponse>()
    private val liveDataForgotPassword = SingleLiveEvent<ForgotPasswordRes>()
    private val liveDataVerifyEmail = SingleLiveEvent<VerifyEmailResModel>()
    private val liveDataVerifyMobile = SingleLiveEvent<VerifyMobileResModel>()
    private val liveDataUpdateSocialRes = SingleLiveEvent<LoginResponse>()

    private val liveDataUnReadChatCount = SingleLiveEvent<UnReadChatMessageResModel>()
    private val liveDataUnseenNotificationCount = SingleLiveEvent<TotalUnseenNotificationResModel>()

    val getMessage
        get() : LiveData<String>
        = liveDataMessage

    val getProgress
        get() : LiveData<Boolean>
        = liveDataProgress

    val getRegisterResponse
        get() :LiveData<LoginResponse>
        = liveDataRegisterResponse


    val getUnSeenNotificationCount
        get() : LiveData<TotalUnseenNotificationResModel>
                =liveDataUnseenNotificationCount

    val getUnReadChatCount
        get() : LiveData<UnReadChatMessageResModel>
                = liveDataUnReadChatCount

    val getVerifyOtpObserver : LiveData<VerifyMobileResModel> get() = liveDataVerifyMobile



    val getForgotPasswordRes
        get() : LiveData<ForgotPasswordRes>
        = liveDataForgotPassword

    val getUpdateSocialObserver: LiveData<LoginResponse> get() = liveDataUpdateSocialRes
    val getVerifyEmailObserver: LiveData<VerifyEmailResModel> get() = liveDataVerifyEmail

    val getChangePasswordObserver: LiveData<GlobalResModel> get() = liveDataChangePassword


    fun registerUser(
        name: String,
        email: String,
        deviceId: String,
        mobile: String,
        countryCode: String,
        password: String,
        confirmPassword: String,
        deviceToken:String,
    ) {
        if (!Global.hasInternet(mContext)) return
        scope.launch {

            when {
                TextUtils.isEmpty(name) -> liveDataMessage.postValue(mContext.getString(R.string.messageNameRequired))
                TextUtils.isEmpty(email) -> liveDataMessage.postValue(mContext.getString(R.string.messageEmailRequired))
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> liveDataMessage.postValue(
                    mContext.getString(R.string.messageValidEmailAddress)
                )
                TextUtils.isEmpty(mobile) -> liveDataMessage.postValue(mContext.getString(R.string.messageMobileRequired))
                TextUtils.isEmpty(password) -> liveDataMessage.postValue(mContext.getString(R.string.messagePasswordRequired))
                password.length < 8 -> liveDataMessage.postValue(mContext.getString(R.string.messageMinimumPasswordLenght))
                password != confirmPassword -> {

                    liveDataMessage.postValue(mContext.getString(R.string.messagePasswordDoesNotMatch))
                }

                else -> {
                    liveDataProgress.postValue(true)
                    val result = authRepo.registerUser(
                        name,
                        email,
                        deviceId,
                        "$countryCode$mobile",
                        password,
                        deviceToken

                    )

                    result?.let {

                        if (it.success == Constants.RESPONSE_SUCCESS){
                            liveDataRegisterResponse.postValue(result!!)
                        }else{
                            liveDataMessage.postValue(it.messageText)
                        }
                    }?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
                    liveDataProgress.postValue(false)
                }
            }
        }
    }

    fun loginUser(
        email: String,
        password: String,
        deviceId: String,
        deviceToken: String
    ) {
        if (!Global.hasInternet(mContext)) return
        scope.launch {

            when {
                TextUtils.isEmpty(email) -> liveDataMessage.postValue(mContext.getString(R.string.messageEmailRequired))
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> liveDataMessage.postValue(
                    mContext.getString(R.string.messageValidEmailAddress)
                )
                TextUtils.isEmpty(password) -> liveDataMessage.postValue(mContext.getString(R.string.messagePasswordRequired))

                else -> {
                    liveDataProgress.postValue(true)
                    val result = authRepo.login(email, password, deviceId,deviceToken)
                    if (result?.success == Constants.RESPONSE_SUCCESS) {
                        liveDataMessage.postValue(result.messageText)
                        liveDataRegisterResponse.postValue(result!!)
                    } else {
                        liveDataMessage.postValue(
                            result?.messageText
                                ?: mContext.getString(R.string.somethingWentWrong)
                        )
                    }

                    liveDataProgress.postValue(false)
                }
            }

        }
    }

    fun forgotPassword(
        email: String
    ) {
        if (!Global.hasInternet(mContext)) return

        scope.launch {
            when {
                (TextUtils.isEmpty(email)) -> liveDataMessage.postValue(mContext.getString(R.string.messageEmailRequired))
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> liveDataMessage.postValue(
                    mContext.getString(R.string.messageValidEmailAddress)
                )
                else -> {
                    liveDataProgress.postValue(true)
                    val result = authRepo.forgotPassword(email)
                    result?.let {

                        if (it.success == Constants.RESPONSE_SUCCESS) {
                            liveDataForgotPassword.postValue(it)
                        } else {
                            liveDataMessage.postValue(it.message)

                        }

                    } ?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))


                    liveDataProgress.postValue(false)
                }

            }


        }

    }

    fun socialLogin(
        email: String,
        profileId: String,
        deviceToken: String,
        name: String,
        mobile: String,
        facebookId: String,
        gmailId: String,
        platform: String,
        userImage: String = ""

    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }
        scope.launch {
            liveDataProgress.postValue(true)
            val result =
                authRepo.socialLogin(
                    email,
                    profileId,
                    deviceToken,
                    name,
                    mobile,
                    facebookId,
                    gmailId,
                    platform,
                    userImage
                )
            result?.let {
                liveDataRegisterResponse.postValue(result!!)
            } ?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
            liveDataProgress.postValue(false)
        }
    }





    fun verifyEmail(email: String) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (email.isEmpty()) {
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = authRepo.verifyEmail(email)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataVerifyEmail.postValue(it)
                } else {
                    liveDataMessage.postValue(it.message)
                }

            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }
    }

    fun verifyMobile(mobile:String){
        if(!Global.hasInternet(mContext)){
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }
        if(mobile.isEmpty()){
            return
        }
        scope.launch {
            liveDataProgress.postValue(true)
            var result = authRepo.verifyMobile(mobile)
            result?.let {
                if(it.success==Constants.RESPONSE_SUCCESS){
                    liveDataVerifyMobile.postValue(it)
                }else{
                    liveDataMessage.postValue(it.messageText)
                }
            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }

    }

    fun updateSocial(
        userId: String,
        facebookId: String,
        googleId: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }
        if (userId.isEmpty()) {
            liveDataMessage.postValue(mContext.getString(R.string.messageLoginToApp))
            return
        }
        if (facebookId.isEmpty()) {
            liveDataMessage.postValue("Error while linking With facebook")
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = authRepo.updateSocial(userId, facebookId, googleId)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataUpdateSocialRes.postValue(it)
                } else {
                    liveDataMessage.postValue(it.messageText)
                }

            }
            liveDataProgress.postValue(false)
        }
    }


    fun changePassword(
        userId: String,
        oldPassword: String,
        newPassword: String
    ) {

        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty()) {
            Log.d(TAG, "changePassword: USER ID IS EMPTY ==== YOUR USER ID IS => $userId")
            return
        }
        scope.launch {
            liveDataProgress.postValue(true)
            val result = authRepo.changePassword(userId, oldPassword, newPassword)
            result?.let {
                liveDataChangePassword.postValue(it)
                 liveDataMessage.postValue(it.message)
            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }


    }


    fun productDetailAvailable(
        id:String
    ){
        if(!Global.hasInternet(mContext)){
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }
        scope.launch {
            liveDataProgress.postValue(true)
            val result = authRepo.productDetailAvailable(id)
            result?.let {
                liveDataMessage.postValue(it.message)
            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }
    }

    fun unReadChatCount(
        userId:String
    ){
       if(!Global.hasInternet(mContext)){
           liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
           return
       }
        scope.launch {
            liveDataProgress.postValue(true)
            val result=authRepo.unReadChatMessage(userId)
            result?.let {
                if(it.success==Constants.RESPONSE_SUCCESS){
                    liveDataUnReadChatCount.postValue(it)
                }else{
                    liveDataMessage.postValue(it.messageText)
                }
            } ?:liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }
    }

    fun toTalUnseenNotificatio(
        userId:String
    ){
        if(!Global.hasInternet(mContext)){
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }
        scope.launch {
            liveDataProgress.postValue(true)
            val result=authRepo.tOtalUnseenNotification(userId)
            result?.let {
                if(it.success==Constants.RESPONSE_SUCCESS){
                    liveDataUnseenNotificationCount.postValue(it)
                }else{
                    liveDataMessage.postValue(it.messageText)
                }
            } ?:liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }
    }

}