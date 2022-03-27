package com.ichota.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.*
import com.ichota.network.APIFactory
import com.ichota.repositories.TotalRatingItemRepository
import com.ichota.utils.Global
import com.ichota.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class TotalRatingItemViewMOdel(application:Application) : AndroidViewModel(application) {

    private val mContext get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext : CoroutineContext get() = parentJob+Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    private val totalrationgitemrepo=TotalRatingItemRepository(APIFactory.makeServiceAPi())


    private  val liveDataMessage= SingleLiveEvent<String>()
    private val liveDataProgress= SingleLiveEvent<Boolean>()
    private val records= ArrayList<Record>()
    private val liveDataTotalReviewItem=MutableLiveData<RecordData>()


    val getMessageObserver
        get() : LiveData<String>
        = liveDataMessage

    val getProgressObserver
        get()  :  LiveData<Boolean>
        = liveDataProgress

    val getNotificationObserver
        get()  : LiveData<RecordData>
        = liveDataTotalReviewItem

    fun getTotalReviewItem(
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
            records.clear()
            val result = totalrationgitemrepo.getTotalRatingItem(userId)
            result?.let {
                records.addAll(result.data.records)
                liveDataTotalReviewItem.postValue(result.data)

            } ?: liveDataMessage.postValue(mContext.getString(R.string.thisuseriddoesntexit))
            liveDataProgress.postValue(false)
        }

    }

}