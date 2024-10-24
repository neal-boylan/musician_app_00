package org.wit.musician_00.models

import androidx.media3.extractor.text.webvtt.WebvttCssStyle.FontSizeUnit
import timber.log.Timber.i

class UserMemStore : UserStore {
    private val users = ArrayList<UserModel>()

    override fun findAll(): List<UserModel> {
        return users
    }

    override fun findByEmail(email: String): UserModel? {
        val foundUser: UserModel? = users.find { it.email == email }
        return foundUser
    }

    override fun create(user: UserModel) {
        users.add(user)
        logAll()
    }

    override fun update(user: UserModel) {
        val foundUser: UserModel? = users.find { u -> u.email == user.email }
        if (foundUser != null) {
            foundUser.email = user.email
            foundUser.password = user.password
            logAll()
        }
    }

    private fun logAll() {
        users.forEach{ i("$it") }
    }

}