package org.wit.musician_00.helpers

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import org.wit.musician_00.R

fun showImagePicker(intentLauncher : ActivityResultLauncher<Intent>) {
    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.type = "image/*"
    chooseFile = Intent.createChooser(chooseFile, R.string.select_clip_image.toString())
    intentLauncher.launch(chooseFile)
}