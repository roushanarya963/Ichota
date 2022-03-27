package com.ichota.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.AddPostResModel
import com.ichota.model.UploadPostModel
import com.ichota.network.APIFactory
import com.ichota.repositories.AddServiceRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import kotlin.coroutines.CoroutineContext

class AddServiceViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext: Context get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val serviceImages = ArrayList<String>()
    private var mCoverImage: String = ""
    private var mCoverImgPosition: Int = -1
    private val mUploadPostData = UploadPostModel()
    private val addServiceRepo = AddServiceRepository(APIFactory.makeServiceAPi())


    private val liveDataServiceImages = MutableLiveData<ArrayList<String>>()
    private val liveDataCoverImage = MutableLiveData<String>()
    private val liveDataProgress = SingleLiveEvent<Boolean>()
    private val liveDataMessage = SingleLiveEvent<String>()
    private val liveDataAddService = SingleLiveEvent<AddPostResModel>()

    val getPostData : UploadPostModel get() = mUploadPostData
    val getServiceImages: ArrayList<String> get() = serviceImages




    val getLiveDataImages
        get() : LiveData<ArrayList<String>>
        = liveDataServiceImages

    val getCoverImageObserver
        get() : LiveData<String>
        = liveDataCoverImage


    val getProgressObserver
        get() : LiveData<Boolean>
        = liveDataProgress

    val getMessageObserver
        get()  :LiveData<String>
        = liveDataMessage

    val getAddServiceObserver
        get() : LiveData<AddPostResModel>
        = liveDataAddService


    fun addImage(image: String) {
        if (serviceImages.isEmpty()) {
            mCoverImgPosition = 0
            mCoverImage = image
            liveDataCoverImage.postValue(mCoverImage)
        }
        serviceImages.add(image)
        liveDataServiceImages.postValue(serviceImages)
    }

    fun setCoverImage(image: String, position: Int) {
        mCoverImgPosition = position
        mCoverImage = image
        liveDataCoverImage.postValue(mCoverImage)
    }

    fun removeAllImages() {
        serviceImages.clear()
        setCoverImage("",-1)
        liveDataServiceImages.postValue(serviceImages)

    }

    fun removeImage(position: Int) {
        if (serviceImages.size > 1) {
            serviceImages.removeAt(position)
            liveDataServiceImages.postValue(serviceImages)
            if (position == mCoverImgPosition) {
                setCoverImage(serviceImages[0], 0)
            }


        } else {
            removeAllImages()
        }


    }




    fun postService() {
        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)
            mUploadPostData.productCoverImage =
                Global.prepareFilePart("service_cover_image", mCoverImage)

            val postImagesPart = ArrayList<MultipartBody.Part?>()

            for (image in serviceImages) {
                postImagesPart.add(Global.prepareFilePart("service_img[]", image))
            }
            mUploadPostData.productImg = postImagesPart
            val result = addServiceRepo.addService(mUploadPostData)
            result?.let {
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataAddService.postValue(it)

                } else {
                    liveDataMessage.postValue(it.message)

                }
            }?:liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))


            liveDataProgress.postValue(false)

        }
    }


}