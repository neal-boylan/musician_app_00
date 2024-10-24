package org.wit.musician_00.models

interface UserStore {
    fun findAll(): List<UserModel>
    fun findByEmail(email:String) : UserModel?
    fun create(user: UserModel)
    fun update(user: UserModel)
}