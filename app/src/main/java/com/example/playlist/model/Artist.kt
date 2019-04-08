package com.example.playlist.model

import java.io.Serializable

class Artist(): Serializable {

    // name
    private var artist: String = ""

    // image
    private var image: String = ""

    // bio
    private var bio: String = ""

    // url
    private var url: String = ""

    constructor(
        artist: String,
        image: String,
        bio: String,
        url: String
    ): this() {
        this.artist = artist
        this.image = image
        this.bio = bio
        this.url = url
    }

    //  GET METHOD
    fun getArtist(): String {
        return this.artist
    }

    fun getImage(): String {
        return this.image
    }

    fun getBio(): String {
        return this.bio
    }

    fun getUrl(): String {
        return this.url
    }
}