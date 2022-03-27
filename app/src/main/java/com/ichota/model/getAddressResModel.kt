package com.ichota.model

import com.google.gson.annotations.SerializedName

data class GetAddressResModel(
    @SerializedName("latlong")
    val latLong : LatLong
)

data class LatLong(
    @SerializedName("Latitude")
    val lattitude : String,
    @SerializedName("Longitude")
    val longitude : String

)
