package org.wit.musician_00.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class ClipModel(
    var id: Long = 2,
    var userId: Long = 0,
    var title: String = "",
    var description: String = "",
    var instrument: String = "",
    var yearsOfExperience: Number = 0,
    var influences: ArrayList<String> = arrayListOf(),
    var genres: ArrayList<String> = arrayListOf("Metal"),
    var image: Uri = Uri.EMPTY,
    var audio: Uri = Uri.EMPTY,
    var clipDate: String = "Date Added: ${LocalDate.now()}",
    var clipEditDate: String = "",
    var location: Location = Location(52.245696, -7.139102, 5f)) : Parcelable
@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable