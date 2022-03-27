package com.ichota.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.*
import com.ichota.repositories.PostDetailRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.network.APIFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext



class PostDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val mContext get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val postDetailRepo = PostDetailRepository(APIFactory.makeServiceAPi())
    private var mPosts = ArrayList<Post>()
    private var mServicePost = ArrayList<ServicePostDetail>()
    private val liveDataMessage = MutableLiveData<String>()
    private val liveDataProgress = MutableLiveData<Boolean>()
    private val liveDataPost = MutableLiveData<Post>()
    private val liveDataServicePost = MutableLiveData<ServicePostDetail>()
    private val liveDataAddToBid = MutableLiveData<AddBidResModel>()


    val getPost get() = mPosts

    val getServicePost get() = mServicePost

    val getMessageObserver
        get()  : LiveData<String>
        = liveDataMessage

    val getProgressObserver
        get() : LiveData<Boolean>
        = liveDataProgress



    val getPostDetailObserver
        get() : LiveData<Post>
        = liveDataPost

    val getServicePostDetailObserver
        get() : LiveData<ServicePostDetail>
        = liveDataServicePost


    val getAddToBidObserver
        get() : LiveData<AddBidResModel>
        = liveDataAddToBid


    fun getPostDetail(userId : String,postId: String) {
        if (!Global.hasInternet(mContext)) return
        scope.launch {
            liveDataProgress.postValue(true)
            val result = postDetailRepo.getPostDetail(userId,postId)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    mPosts = it.data
                    liveDataPost.postValue(it.data[0])

                } else {
                    liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
                    liveDataMessage.postValue(it.message)
                }
            }

            liveDataProgress.postValue(false)
        }
    }


    fun getServiceDetail(userId : String,serviceId: String) {
        if (!Global.hasInternet(mContext)) return
        scope.launch {
            liveDataProgress.postValue(true)
            val result = postDetailRepo.getServiceDetail(userId,serviceId)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    mServicePost = it.data
                    liveDataServicePost.postValue(it.data[0])
                } else {

                    liveDataMessage.postValue(it.message)
                }


            }?:liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))

            liveDataProgress.postValue(false)
        }
    }




    fun addToBid(
        userId: String,
        postId: String,
        bidAmount: String
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
            val result = postDetailRepo.addToBid(userId, postId, bidAmount)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataAddToBid.postValue(it)
                }
                liveDataMessage.postValue(it.message)
            }?:liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))

            liveDataProgress.postValue(false)
        }

    }
}


