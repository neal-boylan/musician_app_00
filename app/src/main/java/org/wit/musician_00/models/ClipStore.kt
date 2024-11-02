package org.wit.musician_00.models

interface ClipStore {
    fun findAll(): List<ClipModel>
    fun create(clip: ClipModel)
    fun update(clip: ClipModel)
    fun delete(clip: ClipModel)
    fun deleteAll(clipList: List<ClipModel>)
}