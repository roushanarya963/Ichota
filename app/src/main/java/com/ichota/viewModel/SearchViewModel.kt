package com.ichota.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.SalePost
import com.ichota.model.SearchPost
import com.ichota.repositories.SearchRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.network.APIFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext: Context get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope((coroutineContext))
    private val searchRepo = SearchRepository(APIFactory.makeServiceAPi())

    private val liveDataProgress = MutableLiveData<Boolean>()
    private val liveDataMessage = MutableLiveData<String>()
    private val liveDataSearch = MutableLiveData<List<SearchPost>>()

    val getProgressObserver
        get() : LiveData<Boolean>
        = liveDataProgress

    val getMessageObserver
        get() : LiveData<String>
        = liveDataMessage

    val getSearchObserver
        get() : LiveData<List<SearchPost>>
        = liveDataSearch


    fun getSearch(
        latitude:String,
        longitude:String,
        distance:Float,
        searchText: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataSearch.postValue(ArrayList())
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (searchText.isEmpty()) {
            liveDataSearch.postValue(ArrayList())
            liveDataMessage.postValue(mContext.getString(R.string.messageSearchKeyworkRequired))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = searchRepo.getSearchData(latitude,longitude,distance,searchText)

            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataSearch.postValue(result.searchPosts)

                } else {
                    liveDataSearch.postValue(ArrayList())
                }

            }

            liveDataProgress.postValue(false)
        }

    }
}