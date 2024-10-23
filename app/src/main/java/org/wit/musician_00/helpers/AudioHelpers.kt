package org.wit.musician_00.helpers

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import org.wit.musician_00.R

fun showAudioPicker(intentLauncher : ActivityResultLauncher<Intent>) {
    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
    chooseFile.type = "audio/*"
    chooseFile = Intent.createChooser(chooseFile, R.string.select_clip_audio.toString())
    intentLauncher.launch(chooseFile)
}