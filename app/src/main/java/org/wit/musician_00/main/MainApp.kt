package org.wit.musician_00.main

import android.app.Application
import org.wit.musician_00.models.ClipMemStore
import org.wit.musician_00.models.ClipModel
import org.wit.musician_00.models.UserMemStore
import org.wit.musician_00.models.UserModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    // val clips = ArrayList<ClipModel>()
    val clips = ClipMemStore()
    val users = UserMemStore()

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        i("Musician started")
        users.create(UserModel(email = "u", password = "1"))
        clips.create(ClipModel(title = "One", description = "About one..."))
        clips.create(ClipModel(title = "Two", description = "About two..."))
        clips.create(ClipModel(title = "Three", description = "About three..."))
    }
}