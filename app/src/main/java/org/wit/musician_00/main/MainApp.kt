package org.wit.musician_00.main

import android.app.Application
import org.wit.musician_00.models.ClipJSONStore
import org.wit.musician_00.models.ClipMemStore
import org.wit.musician_00.models.ClipModel
import org.wit.musician_00.models.ClipStore
import org.wit.musician_00.models.UserJSONStore
import org.wit.musician_00.models.UserMemStore
import org.wit.musician_00.models.UserModel
import org.wit.musician_00.models.UserStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    // val clips = ArrayList<ClipModel>()
    // val clips = ClipMemStore()
    // val users = UserMemStore()

    lateinit var clips: ClipStore
    lateinit var users: UserStore

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        i("Musician started")
        clips = ClipJSONStore(applicationContext)
        users = UserJSONStore(applicationContext)

//        clips = ClipMemStore()
//        users = UserMemStore()
//        users.create(UserModel(email = "u", password = "1"))
//        clips.create(ClipModel(title = "One", description = "About one..."))
//        clips.create(ClipModel(title = "Two", description = "About two..."))
//        clips.create(ClipModel(title = "Three", description = "About three..."))
    }
}