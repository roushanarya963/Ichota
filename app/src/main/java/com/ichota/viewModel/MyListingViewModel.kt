package com.ichota.viewModel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.model.*
import com.ichota.repositories.MyListingRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.network.APIFactory
import com.ichota.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MyListingViewModel(application: Application) : AndroidViewModel(application) {
    private val mContext: Context get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope: CoroutineScope = CoroutineScope(coroutineContext)
    private val myListingRepo = MyListingRepository(APIFactory.makeServiceAPi())
    private val liveDataProgress = MutableLiveData<Boolean>()
    private val liveDataMarkAsSold = SingleLiveEvent<GlobalResModel>()

    private val liveDataMarkAsDeleted = SingleLiveEvent<GlobalResModel>()

    private val liveDataMessage = MutableLiveData<String>()
    private val liveDataSoldAds = MutableLiveData<ArrayList<SoldPost>>()
    private val liveDataActiveAds = MutableLiveData<ArrayList<ActivePost>>()

    private val liveDataMarkAndSoldobs = MutableLiveData<ArrayList<UserChatData>>()

    val getProgressObserver
        get() : LiveData<Boolean>
        = liveDataProgress



    val getMessageObserver
        get()  : LiveData<String>
        = liveDataMessage

    val getSoldAdsObserver
        get()  : LiveData<ArrayList<SoldPost>>
        = liveDataSoldAds


    val getActiveAdsObserver
        get()  : LiveData<ArrayList<ActivePost>>
        = liveDataActiveAds


    val getMarkAndSoldObserver
         get() : LiveData<ArrayList<UserChatData>>
             = liveDataMarkAndSoldobs

    val getMarkAsSoldObserver: LiveData<GlobalResModel> get() = liveDataMarkAsSold
    val getMarkAsDeletedObserver: LiveData<GlobalResModel> get() = liveDataMarkAsDeleted


    fun getSoldAds(userId: String) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }


        if (userId.isEmpty()) {
            liveDataSoldAds.postValue(ArrayList())
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = myListingRepo.getSoldAds(userId)
            result?.let {
                liveDataSoldAds.postValue(result.data)

            } ?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
            liveDataProgress.postValue(false)
        }
    }


    fun getActiveAds(userId: String) {

        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty()) {
            liveDataActiveAds.postValue(ArrayList())
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = myListingRepo.getActiveAds(userId)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS){
                    liveDataActiveAds.postValue(result.data)
                }else{
                    liveDataActiveAds.postValue(ArrayList())
                }

            } ?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
            liveDataProgress.postValue(false)
        }
    }

    fun markAsSold(
        postId : String,
        userId : String) {

        if (postId.isEmpty() || userId.isEmpty()){
            return
        }

        if (!Global.hasInternet(mContext)){
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = myListingRepo.markAsSold(postId,userId)
            result?.let {
               if (it.success == Constants.RESPONSE_SUCCESS){
                   liveDataMarkAsSold.postValue(it)
               }
                liveDataMessage.postValue(it.message)

            }?:liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }

    }


    fun markAndSold(
        userId: String,
        productId:String
    ){
        if(userId.isEmpty() || productId.isEmpty()){
            liveDataMarkAndSoldobs.postValue(ArrayList())
            return
        }

        if(!Global.hasInternet(mContext)){
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
        }
        scope.launch {
            liveDataProgress.postValue(true)
            val result = myListingRepo.markAndSold(userId,productId)
            result?.let {
                if(it.success==Constants.RESPONSE_SUCCESS){
                    liveDataMarkAndSoldobs.postValue(result.data)
                }
                liveDataMessage.postValue(it.messageText)
            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }

    }

    fun markAsDeleted(
        postId : String,
        userId : String) {

        if (postId.isEmpty() || userId.isEmpty()){
            return
        }

        if (!Global.hasInternet(mContext)){
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = myListingRepo.markAsDeleted(postId,userId)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS){
                    liveDataMarkAsDeleted.postValue(it)
                }
                liveDataMessage.postValue(it.message)

            }?:liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }

    }


}