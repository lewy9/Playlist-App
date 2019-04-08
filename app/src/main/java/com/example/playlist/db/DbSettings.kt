package com.example.playlist.db

import android.provider.BaseColumns

class DbSettings {
    companion object {
        const val DB_NAME = "playlist.db"
        const val DB_VERSION = 1
    }

    class DBPlaylistEntry: BaseColumns {
        companion object {
            const val TABLE = "playlist"
            const val ID = BaseColumns._ID
            const val TRACK = "track"
            const val ARTIST = "artist"
            const val IMAGE = "image"
            const val PLAYCOUNT = "playCount"
            const val LISTENERS = "listeners"
            const val URL = "url"
        }
    }
}