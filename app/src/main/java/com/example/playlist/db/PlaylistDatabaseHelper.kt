package com.example.playlist.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PlaylistDatabaseHelper(context: Context): SQLiteOpenHelper(context, DbSettings.DB_NAME, null, DbSettings.DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createPlaylistTableQuery = " CREATE TABLE " + DbSettings.DBPlaylistEntry.TABLE + " ( " +
            DbSettings.DBPlaylistEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DbSettings.DBPlaylistEntry.TRACK + " TEXT NOT NULL, " +
            DbSettings.DBPlaylistEntry.ARTIST + " TEXT NOT NULL," +
            DbSettings.DBPlaylistEntry.IMAGE + " TEXT NOT NULL, " +
            DbSettings.DBPlaylistEntry.PLAYCOUNT + " INTEGER NOT NULL, " +
            DbSettings.DBPlaylistEntry.LISTENERS + " INTEGER NOT NULL, " +
            DbSettings.DBPlaylistEntry.URL + " TEXT NOT NULL);"

        db?.execSQL(createPlaylistTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(" DROP TABLE IF EXISTS " + DbSettings.DBPlaylistEntry.TABLE)
        onCreate(db)
    }
}