package org.wit.musician_00.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var userId: Long = 0,
    var email: String = "",
    var password: String = "",
    var userImage: Uri = Uri.EMPTY,
    // var userLocation: UserLocation = UserLocation(52.245696, -7.139102, 15f)
    var lat : Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f,
    var userLocation: Location = Location(lat, lng, zoom)
    ) : Parcelable
@Parcelize
data class UserLocation(var lat: Double = 0.0,
                        var lng: Double = 0.0,
                        var zoom: Float = 0f) : Parcelable