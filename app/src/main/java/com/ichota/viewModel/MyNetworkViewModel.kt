package com.ichota.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.FollowerStatusResModel
import com.ichota.model.GlobalResModel
import com.ichota.model.User
import com.ichota.repositories.MyNetworkRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.SingleLiveEvent
import com.ichota.network.APIFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MyNetworkViewModel(application: Application) : AndroidViewModel(application) {
    private val mContext: Context get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val myNetworkRepo = MyNetworkRepository(APIFactory.makeServiceAPi())
    private val liveDataMessage = MutableLiveData<String>()
    private val liveDataProgress = MutableLiveData<Boolean>()
    private val liveDataFollowers = MutableLiveData<ArrayList<User>>()
    private val liveDataFollowings = MutableLiveData<ArrayList<User>>()
    private val liveDataRemoveItem = SingleLiveEvent<Boolean>()
    private val liveDataCheckFollowerStatus = MutableLiveData<FollowerStatusResModel>()
    private val liveDataToggleFollowUnfollow = MutableLiveData<GlobalResModel>()


    val getMessageObserver
        get()  : LiveData<String>
        = liveDataMessage


    val getProgressObserver
        get() : LiveData<Boolean>
        = liveDataProgress

    val getFollowersObserver
        get() :LiveData<ArrayList<User>>
        = liveDataFollowers

    val getFollowingsObserver
        get()  :LiveData<ArrayList<User>>
        = liveDataFollowings


    val getRemoveItemObserver
        get() : LiveData<Boolean>
        = liveDataRemoveItem


    val getCheckFollowerStatusObserver
        get()  :LiveData<FollowerStatusResModel>
        = liveDataCheckFollowerStatus


    val getToggleFollowUnfollowObserver
        get() :LiveData<GlobalResModel>
        = liveDataToggleFollowUnfollow


    fun getUserFollowers(userId: String) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty()) {
            liveDataFollowers.postValue(ArrayList())
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            val result = myNetworkRepo.getUserFollowers(userId)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataFollowers.postValue(result.data)
                } else {

                    liveDataFollowers.postValue(ArrayList())
                }

            } ?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))

            liveDataProgress.postValue(false)
        }
    }


    fun getUserFollowings(userId: String) {

        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty()) {
            liveDataFollowings.postValue(ArrayList())
            return
        }
        scope.launch {
            liveDataProgress.postValue(true)

            val result = myNetworkRepo.getUserFollowings(userId)
            result?.let {

                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataFollowings.postValue(result.data)
                } else {
                    liveDataFollowings.postValue(ArrayList())
                }

            } ?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))

            liveDataProgress.postValue(false)
        }
    }

    fun removeFollower(
        userId: String,
        followerId: String
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
            val result = myNetworkRepo.removeFollower(userId, followerId)

            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataRemoveItem.postValue(true)
                } else {

                    liveDataRemoveItem.postValue(false)
                    liveDataMessage.postValue(it.message)
                }

            } ?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))



            liveDataProgress.postValue(false)
        }
    }


    fun unFollowUser(
        userId: String,
        followingId: String
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
            val result = myNetworkRepo.unFollowUser(userId, followingId)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataRemoveItem.postValue(true)
                } else {
                    liveDataRemoveItem.postValue(false)
                    liveDataMessage.postValue(it.message)
                }

            } ?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
            liveDataProgress.postValue(false)
        }
    }


    fun checkFollowerStatus(
        userId: String,
        profileId: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty()) {
            liveDataMessage.postValue(mContext.getString(R.string.messageLoginToApp))
            return
        }

        if (profileId.isEmpty()) {
            liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            return
        }
        scope.launch {
            val result = myNetworkRepo.checkFollowerStatus(userId, profileId)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataCheckFollowerStatus.postValue(it)
                } else {
                    liveDataMessage.postValue(it.message)
                }


            }
        }
    }

    fun toggleFollowUnfollow(
        userId: String?,
        followerId: String?
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isNullOrEmpty()) {
            liveDataMessage.postValue(mContext.getString(R.string.messageLoginToApp))
            return
        }

        if (followerId.isNullOrEmpty()) {
            liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            return
        }
        scope.launch {
            val result = myNetworkRepo.toggleFollowUnFollow(userId, followerId)
            result?.let {
                if (result.success == Constants.RESPONSE_SUCCESS) {
                    liveDataToggleFollowUnfollow.postValue(it)
                } else {
                    liveDataMessage.postValue(it.message)
                }
            }
        }

    }
}