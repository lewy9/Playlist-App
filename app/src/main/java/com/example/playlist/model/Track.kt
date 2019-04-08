package com.example.playlist.model

import java.io.Serializable

class Track(): Serializable {
    // track name
    private var track: String = ""

    // track artist
    private var artist: String = ""

    // image
    private var image: String = ""

    // play count
    private var playCount: Int = 0

    // listeners
    private var listeners: Int = 0

    // url
    private var url: String = ""

    var isSaved: Boolean = false

    constructor(
        track: String,
        artist: String,
        image: String,
        playCount: Int,
        listeners: Int,
        url: String
    ): this() {
        this.track = track
        this.artist = artist
        this.image = image
        this.playCount = playCount
        this.listeners = listeners
        this.url = url
    }

    //  GET METHOD
    fun getTrack(): String {
        return this.track
    }

    fun getArtist(): String {
        return this.artist
    }

    fun getImage(): String {
        return this.image
    }

    fun getPlayCounts(): Int {
        return this.playCount
    }

    fun getListeners(): Int {
        return this.listeners
    }

    fun getUrl(): String {
        return this.url
    }

}