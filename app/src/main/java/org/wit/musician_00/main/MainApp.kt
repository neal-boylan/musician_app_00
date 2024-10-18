package org.wit.musician_00.main

import android.app.Application
import org.wit.musician_00.models.ClipModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val clips = ArrayList<ClipModel>()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Musician started")
        clips.add(ClipModel("One", "About one..."))
        clips.add(ClipModel("Two", "About two..."))
        clips.add(ClipModel("Three", "About three..."))
    }
}