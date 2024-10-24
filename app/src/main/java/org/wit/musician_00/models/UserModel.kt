package org.wit.musician_00.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var email: String = "",
    var password: String = "",
    ) : Parcelable
