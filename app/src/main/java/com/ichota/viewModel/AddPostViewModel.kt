package com.ichota.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.AddPostResModel
import com.ichota.model.UploadPostModel
import com.ichota.network.APIFactory
import com.ichota.repositories.AddPostRepository
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import kotlin.coroutines.CoroutineContext

class AddPostViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext: Context get() = getApplication<Application>().applicationContext
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val postImages = ArrayList<String>()
    private var mCoverImage: String = ""

    private val mUploadPostData = UploadPostModel()
    private val addPostRepo = AddPostRepository(APIFactory.makeServiceAPi())
    private var mCoverImgPosition: Int = -1


    private val liveDataPostImages = MutableLiveData<ArrayList<String>>()
    private val liveDataCoverImage = MutableLiveData<String>()
    private val liveDataProgress = SingleLiveEvent<Boolean>()
    private val liveDataMessage = SingleLiveEvent<String>()

    private val liveDataAddPost = SingleLiveEvent<AddPostResModel>()

    val getPostData : UploadPostModel get() = mUploadPostData
    val getPostImage : ArrayList<String> get() = postImages


    val getLiveDataImages
        get() : LiveData<ArrayList<String>>
        = liveDataPostImages

    val getCoverImageObserver
        get() : LiveData<String>
        = liveDataCoverImage


    val getProgressObserver
        get() : LiveData<Boolean>
        = liveDataProgress

    val getMessageObserver
        get()  :LiveData<String>
        = liveDataMessage

    val getAddPostObserver
        get() : LiveData<AddPostResModel>
        = liveDataAddPost


    fun addImage(image: String) {
        if (postImages.isEmpty()) {
            mCoverImgPosition = 0
            mCoverImage = image
            liveDataCoverImage.postValue(mCoverImage)
        }
        postImages.add(image)
        liveDataPostImages.postValue(postImages)
    }

    fun addImage(images: ArrayList<String>) {
        if (postImages.isEmpty()) {
            mCoverImgPosition = 0
            mCoverImage = images.getOrNull(0)?:return
            liveDataCoverImage.postValue(mCoverImage)
        }
        postImages.addAll(images)
        liveDataPostImages.postValue(postImages)
    }

    fun setCoverImage(image: String, position: Int) {
        mCoverImgPosition = position
        mCoverImage = image
        liveDataCoverImage.postValue(mCoverImage)
    }

    fun removeAllImages() {
        postImages.clear()
        setCoverImage("", -1)
        liveDataPostImages.postValue(postImages)

    }

    fun removeImage(position: Int) {
        if (postImages.size > 1) {
            postImages.removeAt(position)
            liveDataPostImages.postValue(postImages)
            if (position == mCoverImgPosition){
                setCoverImage(postImages[0],0)
            }


        } else {
           removeAllImages()
        }



    }






    fun postItem() {

        if (!Global.hasInternet(mContext)) {
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }

        scope.launch {
            liveDataProgress.postValue(true)

            mUploadPostData.productCoverImage = Global.prepareFilePart("product_cover_img", mCoverImage)

            val postImagesPart = ArrayList<MultipartBody.Part?>()

            for (image in postImages) {
                postImagesPart.add(Global.prepareFilePart("product_img[]", image,mContext))
            }

            mUploadPostData.productImg = postImagesPart

            val result = addPostRepo.addPost(mUploadPostData)
            Log.d("TAG", "postItem:$result ")
            result?.let {

                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataAddPost.postValue(it)
                    Log.d("TAG", "postItemResponse:$it ")
                } else {
                    liveDataMessage.postValue(it.message)
                    Log.d("TAG", "postItemResponse:${it.message} ")
                }
            }?: liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
            liveDataProgress.postValue(false)

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

            for (image in postImages) {
                postImagesPart.add(Global.prepareFilePart("service_img[]", image))
            }
            mUploadPostData.productImg = postImagesPart
            val result = addPostRepo.addPost(mUploadPostData)
            result?.let { it
                if (it.success == Constants.RESPONSE_SUCCESS) {
                    liveDataAddPost.postValue(it)
                } else {
                    liveDataMessage.postValue(it.message)
                }
            }?:liveDataMessage.postValue(mContext.getString(R.string.errorFromServer))
            liveDataProgress.postValue(false)
        }
    }


}