package com.ichota.viewModel

import android.app.Application
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ichota.R
import com.ichota.model.GetAddressResModel
import com.ichota.network.APIFactory
import com.ichota.repositories.LocationRepository
import com.ichota.utils.Global
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext: Context get() = getApplication<Application>().applicationContext
    private val parentJob = Job();
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val locationRepo = LocationRepository(APIFactory.makeServiceAPi())

    private val liveDataLocation = MutableLiveData<GetAddressResModel>()
    private val liveDataProgress = MutableLiveData<Boolean>()
    private val liveDataMessage = MutableLiveData<String>()

    val getAddressObserver :LiveData<GetAddressResModel> get() = liveDataLocation
    val getProgressObserver : LiveData<Boolean> get() = liveDataProgress
    val getMessageObserver : LiveData<String> get() = liveDataMessage


    fun getAddressFromZipCode(zipCode : String){
        if (!Global.hasInternet(mContext)){
            liveDataMessage.postValue(mContext.getString(R.string.messageCheckInternet))
            return
        }
        scope.launch {
            liveDataProgress.postValue(true)
            val result = locationRepo.getAddressFromZip(zipCode)

            result?.let {

                liveDataLocation.postValue(result)
            }?:liveDataMessage.postValue(mContext.getString(R.string.somethingWentWrong))
            liveDataProgress.postValue(false)
        }
    }





    fun getAddressFromLatLong(lat: String, lon: String): String {

        return try {
            val lat: Double = lat.toDouble()
            val lon: Double = lon.toDouble()
            val geoCoder = Geocoder(mContext, Locale.getDefault())
            val addresses: List<Address> = geoCoder.getFromLocation(lat, lon, 1)
            val address = addresses[0].getAddressLine(0)
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName

            return "$address,$city,$state,$country,$postalCode,$knownName"


        } catch (e: NumberFormatException) {
            "No Address available"
        }

    }
}