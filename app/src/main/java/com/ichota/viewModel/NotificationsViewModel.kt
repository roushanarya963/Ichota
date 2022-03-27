package com.ichota.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.Notification
import com.ichota.repositories.NotificationRepository
import com.ichota.utils.Global
import com.ichota.utils.SingleLiveEvent
import com.ichota.network.APIFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val notificationRepo = NotificationRepository(APIFactory.makeServiceAPi())
    private val notifications = ArrayList<Notification>()

    private val liveDataMessage = SingleLiveEvent<String>()
    private val liveDataProgress = SingleLiveEvent<Boolean>()
    private val liveDataNotification = MutableLiveData<ArrayList<Notification>>()


    val getMessageObserver
        get() : LiveData<String>
        = liveDataMessage

    val getProgressObserver
        get()  :  LiveData<Boolean>
        = liveDataProgress

    val getNotificationObserver
        get()  : LiveData<ArrayList<Notification>>
        = liveDataNotification

    fun getNotifications(userId: String) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }
        if (userId.isEmpty()) {
            liveDataProgress.postValue(false)
            liveDataNotification.postValue(ArrayList<Notification>())
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            notifications.clear()
            val result = notificationRepo.getNotifications(userId)
            result?.let {
                notifications.addAll(result.data)
                liveDataNotification.postValue(result.data)

            } ?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
            liveDataProgress.postValue(false)
        }
    }
}