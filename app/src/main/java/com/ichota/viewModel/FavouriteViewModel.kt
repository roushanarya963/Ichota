package com.ichota.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.FavouriteItem
import com.ichota.repositories.FavouriteRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.SingleLiveEvent
import com.ichota.network.APIFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class FavouriteViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext: Context get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val favourites = ArrayList<FavouriteItem>()
    private val favouriteRepo = FavouriteRepository(APIFactory.makeServiceAPi())
    private val liveDataMessage = SingleLiveEvent<String>()
    private val liveDataProgress = SingleLiveEvent<Boolean>()
    private val liveDataFavourites = MutableLiveData<ArrayList<FavouriteItem>>()
    private val liveDataFavouriteStatus = MutableLiveData<Boolean>()


    val getMessageObserver
        get() : LiveData<String>
        = liveDataMessage

    val getProgressObserver
        get() : LiveData<Boolean>
        = liveDataProgress
    val getFavouritesObserver
        get() : LiveData<ArrayList<FavouriteItem>>
        = liveDataFavourites

    val getFavouriteStatusObserver
        get() : LiveData<Boolean>
        = liveDataFavouriteStatus


    fun getFavouritePosts(userId: String) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty()) {
            liveDataProgress.postValue(false)
            liveDataFavourites.postValue(ArrayList())
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            favourites.clear()
            val result = favouriteRepo.getFavouriteData(userId)
            result?.let {
                liveDataFavourites.postValue(result.data)
            }?:liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))

            liveDataProgress.postValue(false)
        }
    }


    fun changeFavStatus(
        userId: String,
        productId: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataFavouriteStatus.postValue(false)
            liveDataMessage.postValue("Not connected to Internet")
            return
        }

        if (userId.isNullOrEmpty() or productId.isNullOrEmpty()) {
            liveDataFavouriteStatus.postValue(false)
            liveDataMessage.postValue(mContext.getString(R.string.messageLoginToApp))
            return
        }

        scope.launch {
            val result = favouriteRepo.addToFav(userId, productId)

            result?.let {

                if (it.success == Constants.RESPONSE_SUCCESS) {

                    liveDataFavouriteStatus.postValue(it.status == 1)
                } else {
                    liveDataMessage.postValue(it.message)
                }
            }?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))

        }


    }


    fun changeFavStatusService(
        userId: String,
        postId: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataFavouriteStatus.postValue(false)
            liveDataMessage.postValue("Not connected to Internet")
            return
        }

        if (userId.isNullOrEmpty() or postId.isNullOrEmpty()) {
            liveDataFavouriteStatus.postValue(false)
            liveDataMessage.postValue(mContext.getString(R.string.messageLoginToApp))
            return
        }

        scope.launch {
            val result = favouriteRepo.addToFavService(userId, postId)

            result?.let {

                if (it.success == Constants.RESPONSE_SUCCESS) {

                    liveDataFavouriteStatus.postValue(it.status == 1)
                } else {
                    liveDataMessage.postValue(it.message)
                }
            }?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))

        }


    }


}