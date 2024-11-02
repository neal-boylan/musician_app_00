package org.wit.musician_00.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.wit.musician_00.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val USER_JSON_FILE = "users.json"
val gsonBuilderUser: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParserUser())
    .create()
val listTypeUser: Type = object : TypeToken<ArrayList<UserModel>>() {}.type

fun generateRandomUserId(): Long {
    return Random().nextLong()
}

class UserJSONStore(private val context: Context) : UserStore {

    var users = mutableListOf<UserModel>()

    init {
        if (exists(context, USER_JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<UserModel> {
        logAll()
        return users
    }

    override fun findByEmail(email: String): UserModel? {
        val foundUser: UserModel? = users.find { it.email == email }
        return foundUser
    }

    override fun findByUserId(userId: Long): UserModel? {
        val foundUser: UserModel? = users.find { it.userId == userId }
        return foundUser
    }

    override fun create(user: UserModel) {
        user.userId = generateRandomUserId()
        users.add(user)
        serialize()
    }

    override fun update(user: UserModel) {
        val foundUser: UserModel? = users.find { u -> u.email == user.email }
        if (foundUser != null) {
            foundUser.email = user.email
            foundUser.password = user.password
            foundUser.userImage = user.userImage
            // foundUser.userLocation = user.userLocation
            foundUser.lat = user.lat
            foundUser.lng = user.lng
            foundUser.zoom = user.zoom
            serialize()
        }
    }

    override fun delete(user: UserModel) {
        users.remove(user)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilderUser.toJson(users, listTypeUser)
        write(context, USER_JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, USER_JSON_FILE)
        users = gsonBuilderUser.fromJson(jsonString, listTypeUser)
    }

    private fun logAll() {
        users.forEach { Timber.i("$it") }
    }
}

class UriParserUser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}