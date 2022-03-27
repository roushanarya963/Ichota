package com.ichota.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.SaleCategory
import com.ichota.model.SalePost
import com.ichota.repositories.SaleRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.SingleLiveEvent
import com.ichota.network.APIFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SaleViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val saleRepo = SaleRepository(APIFactory.makeServiceAPi())
    private val salesCategories = ArrayList<SaleCategory>()
    private val salePosts = ArrayList<SalePost>()


    private val liveDataSaleCategories = MutableLiveData<ArrayList<SaleCategory>>()
    private val liveDataSalePosts = MutableLiveData<ArrayList<SalePost>>()
    private val liveDataProgress = SingleLiveEvent<Boolean>()
    private val liveDataMessage = SingleLiveEvent<String>()


    val getSaleResObserver
        get() : LiveData<ArrayList<SaleCategory>>
        = liveDataSaleCategories

    val getProgressObserver
        get() : LiveData<Boolean>
        = liveDataProgress

    val getMessageObserver
        get() :LiveData<String>
        = liveDataMessage

    val getSalePostObserver
        get() : LiveData<ArrayList<SalePost>>
        = liveDataSalePosts

    fun getSaleCategories() {
        if (!Global.hasInternet(mContext)) return
        scope.launch {
            liveDataProgress.postValue(true)

            val result = saleRepo.getSaleCategories()
            result?.let {

                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataSaleCategories.postValue(result.data)
                    salesCategories.addAll(result.data)
                }
                liveDataProgress.postValue(false)

            }?:liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))



        }
    }

    fun getSalePosts(categoryId: String) {
        if (!Global.hasInternet(mContext)) return
        scope.launch {
            liveDataProgress.postValue(true)
            salePosts.clear()
            val result = saleRepo.getSalePosts(categoryId)

            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    salePosts.addAll(result.data)
                    liveDataSalePosts.postValue(result.data)

                } else {
                    liveDataSalePosts.postValue(ArrayList())
                }
            }?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)




        }


    }



    fun searchSalePostWithFilter(
        minimumProductPrice: String,
        maximumProductPrice: String,
        buyingOption:String,
        conditions:String,
        newest:String,
        highToLow:String,
        lowToHigh:String,
        latitude:String,
        longitude:String,
        categoryId:String,
        distance:String
    ){
        if(!Global.hasInternet(mContext)){
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = saleRepo.searchSalePostWithFilter(
                minimumProductPrice,
                maximumProductPrice,
                buyingOption,
                conditions,
                newest,
                highToLow,
                lowToHigh,
                latitude,
                longitude,
                categoryId,
                distance
            )
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS){
                    liveDataSalePosts.postValue(it.data)
                }else{
                    liveDataMessage.postValue(it.message)
                }
            }
            liveDataProgress.postValue(false)
        }


    }




}