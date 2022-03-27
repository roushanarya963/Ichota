package com.ichota.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.GlobalResModel
import com.ichota.model.SafetyBanner
import com.ichota.network.APIFactory
import com.ichota.repositories.SafetyCenterRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SafetyCenterViewModel(application: Application) : AndroidViewModel(application) {
    private val mContext: Context get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope: CoroutineScope = CoroutineScope(coroutineContext)
    private val safetyCenterRepo = SafetyCenterRepository(APIFactory.makeServiceAPi())


    private val liveDataMessage = MutableLiveData<String>()
    private val liveDataProgress = MutableLiveData<Boolean>()
    private val liveDataSafetyBanners = MutableLiveData<List<SafetyBanner>>()
    private val liveDataReportSafetyIssue = SingleLiveEvent<GlobalResModel>()

    val getMessageObserver: LiveData<String> get() = liveDataMessage
    val getProgressObserver: LiveData<Boolean> get() = liveDataProgress
    val getSafetyBannersObserver: LiveData<List<SafetyBanner>> get() = liveDataSafetyBanners
    val getReportSafetyObserver : LiveData<GlobalResModel> get() = liveDataReportSafetyIssue


    fun getSafetyBanners(userId: String?) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId == null) return

        scope.launch {
            liveDataProgress.postValue(true)
            val result = safetyCenterRepo.getSafetyBanners(userId)
            liveDataProgress.postValue(false)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataSafetyBanners.postValue(it.safetyBanners)
                } else {
                    liveDataMessage.postValue(it.message)
                }
            }
        }

    }


    fun reportSafetyIssue(
        userId : String,
        reportUserId:String,
        productId:String,
        serviceId:String,
        comments:String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }


        scope.launch {
            liveDataProgress.postValue(true)
            val result = safetyCenterRepo.reportSafetyIssue(userId,reportUserId, productId, serviceId, comments)
            liveDataProgress.postValue(false)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataReportSafetyIssue.postValue(it)
                } else {
                    liveDataMessage.postValue(it.message)
                }
            }
        }

    }
}