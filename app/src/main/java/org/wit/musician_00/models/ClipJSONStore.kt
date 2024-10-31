package org.wit.musician_00.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.wit.musician_00.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "clips.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<ClipModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class ClipJSONStore(private val context: Context) : ClipStore {

    var clips = mutableListOf<ClipModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<ClipModel> {
        logAll()
        return clips
    }

    override fun create(clip: ClipModel) {
        clip.id = generateRandomId()
        clips.add(clip)
        serialize()
    }


    override fun update(clip: ClipModel) {
        var foundClip: ClipModel? = clips.find { c -> c.id == clip.id }
        if (foundClip != null) {
            foundClip.title = clip.title
            foundClip.userId = clip.userId
            foundClip.description = clip.description
            foundClip.yearsOfExperience = clip.yearsOfExperience
            foundClip.instrument = clip.instrument
            foundClip.influences = clip.influences
            foundClip.genres = clip.genres
            foundClip.clipDate = clip.clipDate
            foundClip.clipEditDate = clip.clipEditDate
            foundClip.image = clip.image
            foundClip.audio = clip.audio
            foundClip.location = clip.location
            serialize()
        }
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(clips, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        clips = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        clips.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
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