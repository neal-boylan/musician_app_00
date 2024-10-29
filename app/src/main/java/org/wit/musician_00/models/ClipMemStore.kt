package org.wit.musician_00.models

import androidx.media3.extractor.text.webvtt.WebvttCssStyle.FontSizeUnit
import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class ClipMemStore : ClipStore {
    val clips = ArrayList<ClipModel>()

    override fun findAll(): List<ClipModel> {
        return clips
    }

    override fun create(clip: ClipModel) {
        clip.id = getId()
        clips.add(clip)
        logAll()
    }

    override fun update(clip: ClipModel) {
        var foundClip: ClipModel? = clips.find { c -> c.id == clip.id }
        if (foundClip != null) {
            foundClip.title = clip.title
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
            logAll()
        }
    }

    private fun logAll() {
        clips.forEach{ i("$it") }
    }

}