package com.ichota.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.*
import com.ichota.network.APIFactory
import com.ichota.repositories.ProfileRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import kotlin.coroutines.CoroutineContext

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val mContext: Context get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val profileRepo = ProfileRepository(APIFactory.makeServiceAPi())
    private val liveDataProgress = MutableLiveData<Boolean>()
    private val liveDataMessage = MutableLiveData<String>()


    private val livedataChatDetail = MutableLiveData<ArrayList<PostDetail>>()

    private val liveDataMarketPlace = MutableLiveData<ArrayList<MarketPlacePost>>()
    private val liveDataProfile = MutableLiveData<ArrayList<User>>()
    private val liveDataUpdateImage = MutableLiveData<ProfileImageResModel>()
    private val liveDataUpdateEmergencyContact = MutableLiveData<UpdateEmergencyContactModel>()
    private val liveDataUpdateUserDetail = MutableLiveData<User>()
    private val liveDataSendOtpToEmail = MutableLiveData<GlobalResModel>()
    private val liveDataPaymentMethods = MutableLiveData<List<PaymentMethod>>()
    private val liveDataSalePosts = MutableLiveData<List<SalePost>>()
    private val liveDataServicePosts = MutableLiveData<List<ServicePost>>()
    private val liveDataUpdatePaymentMethod = MutableLiveData<GlobalResModel>()





    val getProgressObserver
        get() : LiveData<Boolean>
        = liveDataProgress

    val getMessageObserver
        get()  : LiveData<String>
        = liveDataMessage

    val getMarketPlaceObserver
        get() : LiveData<ArrayList<MarketPlacePost>>
        = liveDataMarketPlace

    val getUserObserver
        get() :LiveData<ArrayList<User>>
        = liveDataProfile

    val getProfileUpdateObserver
        get() :LiveData<ProfileImageResModel>
        = liveDataUpdateImage


    val getEmergencyContactObserver
        get() : LiveData<UpdateEmergencyContactModel>
        = liveDataUpdateEmergencyContact

    val getUpdateUserDetailObserver
        get(): LiveData<User> = liveDataUpdateUserDetail

    val getSendOtpToEmailObserver get() : LiveData<GlobalResModel> = liveDataSendOtpToEmail
    val getPaymentMethodsObserver: LiveData<List<PaymentMethod>> get() = liveDataPaymentMethods

    val getSalePostsObserver: LiveData<List<SalePost>> get() = liveDataSalePosts
    val getServicePostsObserver: LiveData<List<ServicePost>> get() = liveDataServicePosts
    val getUpdatePaymentMethodObserver: LiveData<GlobalResModel> get() = liveDataUpdatePaymentMethod


    fun getMarketPlacePosts(
        userId: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty()) {
            liveDataMarketPlace.postValue(ArrayList())
            liveDataProgress.postValue(false)
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = profileRepo.getMarketPlacePosts(userId)

            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataMarketPlace.postValue(result.data)
                } else {
                    liveDataMarketPlace.postValue(ArrayList())
                }

            } ?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
            liveDataProgress.postValue(false)
        }

    }

    fun getUserProfile(
        userId: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty()) {
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = profileRepo.getUserProfile(userId)
            Log.d("TAG", "getUserProfile: $result")
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataProfile.postValue(result.data)
                } else {
                    liveDataMessage.postValue(it.messageText)
                }
            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))

            liveDataProgress.postValue(false)
        }
    }


    fun updateProfileImage(
        userId: String,
        profileImage: MultipartBody.Part?
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }
        if (userId.isEmpty()) {
            liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = profileRepo.updateProfileImage(
                userId, profileImage
            )
            result?.let {
                liveDataUpdateImage.postValue(it)
            }

            liveDataProgress.postValue(false)
        }

    }


    fun updateEmergencyContact(
        userId: String,
        contact: String
    ) {

        Log.d("TAG", "updateEmergencyContact: $userId   $contact")

        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }
        scope.launch {
            liveDataProgress.postValue(true)
            val result = profileRepo.updateEmergencyContact(userId, contact)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataUpdateEmergencyContact.postValue(it)
                } else {
                    liveDataMessage.postValue(it.message)
                }


            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)


        }
    }


    fun updateUserDetail(
        request: HashMap<String, String>
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = profileRepo.updateUserDetail(request)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataUpdateUserDetail.postValue(it.user)
                    liveDataMessage.postValue(it.message)
                } else {
                    liveDataMessage.postValue(it.message)
                }
            }
            liveDataProgress.postValue(false)
        }

    }

    fun sendOtpToEmail(
        request: HashMap<String, String>
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = profileRepo.sendOtpToEmail(request)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataSendOtpToEmail.postValue(it)
                    liveDataMessage.postValue(it.message)
                } else {
                    liveDataMessage.postValue(it.message)
                }
            }
            liveDataProgress.postValue(false)
        }

    }


    fun getPaymentMethods(
        userId: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = profileRepo.getPaymentMethods(userId)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataPaymentMethods.postValue(it.paymentMethods)
                } else {
                    liveDataMessage.postValue(it.message)
                }
            }
            liveDataProgress.postValue(false)
        }

    }

    fun updatePaymentMethod(
        userId: String,
        paymentStatus: String,
        paymentId: String,
        paymentUrl: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result =
                profileRepo.updatePaymentMethod(userId, paymentStatus, paymentId, paymentUrl)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataUpdatePaymentMethod.postValue(it)
                } else {
                    liveDataMessage.postValue(it.message)
                }
            }
            liveDataProgress.postValue(false)
        }

    }


    fun getSalePost(
        userId: String

    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = profileRepo.getUserSalePosts(userId)
            liveDataProgress.postValue(false)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataSalePosts.postValue(it.data)
                } else {
                    liveDataSalePosts.postValue(ArrayList())
                }
            }
        }

    }

    fun getServicePost(
        userId: String

    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = profileRepo.getUserServicePosts(userId)
            liveDataProgress.postValue(false)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataServicePosts.postValue(it.data)
                } else {
                    liveDataServicePosts.postValue(ArrayList())
                }
            }

        }

    }

}