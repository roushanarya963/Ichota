package com.ichota.model

import okhttp3.MultipartBody

data class UploadPostModel(
    var userId: String = "",
    var productName: String = "",
    var productImg: ArrayList<MultipartBody.Part?> = ArrayList(),
    var productCoverImage: MultipartBody.Part? = null,
    var productDescription: String = "",
    var productPrice: String = "",
    var firmOnPrice: String = "0",
    var category: String = "",
    var buying_option: String = "",
    var condition: String = "",
    var latitude: String = "0",
    var longitude: String = "0",
    var sellShipNationStatus: String = "0",
    var boxSize: String = "Box(5lb)",
    var categoryId: String = "",
    var brandName: String = "",
    var year: String = "",
    var fuelType: String = "",
    var transmissionType: String = "",
    var kmDriven: String = "",
    var noOfOwners: String = "",
    var carTitle: String = "",
    var cardAdditionalInfo: String = "",
    var bidStartTime: String = "",
    var bidEndTime: String = "",
    var vinNumber: String = ""


)

