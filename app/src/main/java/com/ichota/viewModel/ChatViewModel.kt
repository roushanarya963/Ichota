package com.ichota.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.*
import com.ichota.repositories.ChatRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.SingleLiveEvent
import com.ichota.network.APIFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import kotlin.coroutines.CoroutineContext

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext: Context get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    private val chatRepo = ChatRepository(APIFactory.makeServiceAPi())

    private val liveDataMessage = SingleLiveEvent<String>()
    private val liveDataProgress = SingleLiveEvent<Boolean>()
    private val liveDataMarkAsRead = SingleLiveEvent<MarkAllAsReadResModel>()

    private val liveDataRecentChatDialog = SingleLiveEvent<ArrayList<ChatDialog>>()
    private val liveDataInsertChatRes = SingleLiveEvent<InsertChatResModel>()
    private val liveDataViewChat = MutableLiveData<ViewChatResModel>()
    private val liveDataSaveChatImage = MutableLiveData<GlobalResModel>()
    private val unreadChats = ArrayList<ChatDialog>()
    private val allChats = ArrayList<ChatDialog>()


    val getMessageObserver
        get() :LiveData<String>
        = liveDataMessage

    val getProgressObserver
        get() : LiveData<Boolean>
        = liveDataProgress

    val getRecentChatDialogObserver
        get() : LiveData<ArrayList<ChatDialog>>
        = liveDataRecentChatDialog

    val getMarkAsReadObserver: LiveData<MarkAllAsReadResModel> get() = liveDataMarkAsRead

    val getUnreadChatDialogs: ArrayList<ChatDialog> get() = unreadChats
    val getAllChatDialogs: ArrayList<ChatDialog> get() = allChats
    val getInsertChatResObserver: LiveData<InsertChatResModel> get() = liveDataInsertChatRes
    val getViewChatObserver: LiveData<ViewChatResModel> get() = liveDataViewChat
    val getSaveChatImageObserver: LiveData<GlobalResModel> get() = liveDataSaveChatImage


    fun getRecentChatDialogs(userId: String) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            liveDataRecentChatDialog.postValue(ArrayList())
            return
        }

        if (userId.isEmpty()) {
            liveDataRecentChatDialog.postValue(ArrayList())
            return
        }
        scope.launch {

            liveDataProgress.postValue(true)
            allChats.clear()
            unreadChats.clear()
            val result = chatRepo.getRecentChatDialogs(userId)
            result?.let {

                if (it.success == Constants.RESPONSE_SUCCESS) {
                    allChats.addAll(result.chatDialogs)
                    liveDataRecentChatDialog.postValue(allChats)


                    for (chat in result.chatDialogs) {
                        if (chat.readStatus == "0")
                            unreadChats.add(chat)
                    }
                } else {
                    liveDataRecentChatDialog.postValue(ArrayList())

                }

            } ?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))

            liveDataProgress.postValue(false)
        }
    }

    fun insertChat(
        userId: String,
        receiverId: String,
        readStatus: String,
        message: String,
        productId: String,
        postType:String,
        messageType: String,
        lat: String,
        lng: String,
        image : MultipartBody.Part?= null,


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
            val result =
                chatRepo.insertChat(
                    userId,
                    receiverId,
                    readStatus,
                    message,
                    productId,
                    postType,
                    messageType,
                    lat,
                    lng,
                    image ,
                )
            result?.let {
                liveDataInsertChatRes.postValue(it)

            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))

        }
    }


    fun viewChat(
        userId: String,
        receiverId: String,
        productId: String,
        postType: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty()) {
            return
        }
        scope.launch {

            val result = chatRepo.viewChat(userId, receiverId, productId,postType)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataViewChat.postValue(it)
                } else {
                    liveDataMessage.postValue(it.message)
                }
            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))

        }
    }

    fun markAllAsRead(userId: String) {
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
            val result = chatRepo.markAllAsRead(userId)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataMarkAsRead.postValue(it)
                }
                liveDataMessage.postValue(it.message)

            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }
    }

    fun readChat(
        userId: String,
        productId: String,
        receiverId: String,
        postType: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty() || productId.isEmpty() || receiverId.isEmpty()) {
            Log.e(
                "TAG",
                "readChat: userId = $userId  ||  productId = $productId ||  receiverId = $receiverId"
            )
            return
        }

        scope.launch {
            chatRepo.readChat(userId, productId, receiverId,postType)
        }
    }

    fun changeUserActiveStatus(
        userId: String,
        status: String
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        if (userId.isEmpty()) {
            Log.d("TAG", "changeUserActiveStatus: userId = $userId")
            return
        }

        scope.launch {
            chatRepo.changeUserActiveStatus(userId, status)
        }
    }


    fun saveChatImage(
        senderId: String,
        receiverId: String,
        productId: String,
        image: MultipartBody.Part,
    ) {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            val result = chatRepo.saveChatImage(senderId, receiverId, productId, image)

            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataSaveChatImage.postValue(it)
                } else {
                    liveDataMessage.postValue(it.message)
                }

            } ?: liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
        }


    }
}