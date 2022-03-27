package com.ichota.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.ReportResModel
import com.ichota.repositories.ReportRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.network.APIFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ReportViewModel(application: Application) : AndroidViewModel(application) {
    private val mContext get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val reportRepo = ReportRepository(APIFactory.makeServiceAPi())

    private val liveDataProgress = MutableLiveData<Boolean>()
    private val liveDataMessage = MutableLiveData<String>()
    private val liveDataReport = MutableLiveData<ReportResModel>()

    val getProgressObserver
        get() : LiveData<Boolean>
        = liveDataProgress

    val getReportObserver
        get() : LiveData<ReportResModel>
        = liveDataReport

    val getMessageObserver
        get() : LiveData<String>
        = liveDataMessage


    fun reportPost(
        userId: String,
        serviceId:String,
        productId:String,
        reportType: String,
        comments: String,
        status: String = "1"

    ) {


        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty()) {
            liveDataMessage.postValue(mContext.getString(R.string.messageLoginToApp))
            return
        }
        scope.launch {
            liveDataProgress.postValue(true)
            val result = reportRepo.reportPost(userId, serviceId,productId, reportType, comments, status)
            if (result?.success == Constants.RESPONSE_SUCCESS) {
                liveDataReport.postValue(result)
            } else {
                liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
            }


            liveDataProgress.postValue(false)
        }

    }

    fun reportUser(
        userId: String,
        reportUserId: String,
        reportType: String,
        comments: String

    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }
        if (userId.isEmpty()) {
            liveDataMessage.postValue(mContext.getString(R.string.messageLoginToApp))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)

            val result = reportRepo.reportUser(userId, reportUserId, comments, reportType)
            result?.let {
                if (result.success == Constants.RESPONSE_SUCCESS) {
                    liveDataReport.postValue(it)
                } else {
                    liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
                }

            }
            liveDataProgress.postValue(false)
        }
    }
}