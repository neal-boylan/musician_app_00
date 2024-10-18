package org.wit.musician_00.main

import android.app.Application
import org.wit.musician_00.models.ClipMemStore
import org.wit.musician_00.models.ClipModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    // val clips = ArrayList<ClipModel>()
    val clips = ClipMemStore()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Musician started")
        clips.create(ClipModel(0, "One", "About one..."))
        clips.create(ClipModel(1, "Two", "About two..."))
        clips.create(ClipModel(2, "Three", "About three..."))
    }
}