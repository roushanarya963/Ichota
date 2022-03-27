package com.ichota.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.*
import com.ichota.repositories.ServiceRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.SingleLiveEvent
import com.ichota.network.APIFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ServiceViewModel(application: Application) : AndroidViewModel(application) {
    private val mContext get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val serviceRepo = ServiceRepository(APIFactory.makeServiceAPi())
    private val serviceCategories = ArrayList<ServiceCategory>()
    private val servicePosts = ArrayList<ServicePost>()

    private val liveDataProgress = SingleLiveEvent<Boolean>()
    private val liveDataMessage = SingleLiveEvent<String>()
    private val liveDataServiceCategories = MutableLiveData<ArrayList<ServiceCategory>>()
    private val liveDataToggleAvailableUnAvailable = SingleLiveEvent<GlobalResModel>()
    private val liveDataServicePosts = MutableLiveData<ArrayList<ServicePost>>()


    val getServiceCategoriesObserver
        get() : LiveData<ArrayList<ServiceCategory>>
        = liveDataServiceCategories

    val getServicePostsObserver
        get() : LiveData<ArrayList<ServicePost>>
        = liveDataServicePosts


    val getToggleAvailableUnavailable
        get() : LiveData<GlobalResModel>
        = liveDataToggleAvailableUnAvailable

    val getProgressObserver
        get()  : LiveData<Boolean>
        = liveDataProgress


    val getMessageObserver
        get() :LiveData<String>
        = liveDataMessage


    fun getServiceCategories() {
        if (!Global.hasInternet(mContext)) return
        scope.launch {
            liveDataProgress.postValue(true)
            serviceCategories.clear()
            val result = serviceRepo.getServiceCategories()
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataServiceCategories.postValue(result.data)
                    serviceCategories.addAll(result.data)

                } else {
                    liveDataMessage.postValue(it.message)
                }
            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }

    }

    fun getServicePosts(
        categoryId: String
    ) {
        if (!Global.hasInternet(mContext)) return
        scope.launch {
            liveDataProgress.postValue(true)
            servicePosts.clear()
            val result = serviceRepo.getServicePosts(categoryId)
            result?.let {

                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataServicePosts.postValue(result.data)
                    servicePosts.addAll(result.data)
                } else {
                    liveDataServicePosts.postValue(ArrayList())
                }
            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }
    }



    fun toggleAvailableUnavailable(
        id:String
    ){
        if(!Global.hasInternet(mContext)){
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }
        scope.launch {
            liveDataProgress.postValue(true)
            val result = serviceRepo.toggleAvailableUnAvailable(id)
            result?.let {
                liveDataToggleAvailableUnAvailable.postValue(it)

            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }
    }

    fun searchServicePostWithFilter(
        minimumProductPrice: String,
        maximumProductPrice: String,
        buyingOption:String,
        conditions:String,
        distance:String,
        newest:String,
        highToLow:String,
        lowToHigh:String,
        latitude:String,
        longitude:String,
        categoryId:String
    ){
        if(!Global.hasInternet(mContext)){
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = serviceRepo.searchServicePostWithFilter(
                minimumProductPrice,
                maximumProductPrice,
                buyingOption,
                conditions,
                distance,
                newest,
                highToLow,
                lowToHigh,
                latitude,
                longitude,
                categoryId
            )
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS){
                    liveDataServicePosts.postValue(it.data)
                }else{
                    liveDataMessage.postValue(it.message)
                }
            }
            liveDataProgress.postValue(false)
        }


    }



}