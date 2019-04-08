package com.example.playlist.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.example.playlist.db.DbSettings
import com.example.playlist.db.PlaylistDatabaseHelper
import com.example.playlist.model.Artist
import com.example.playlist.model.Track
import com.example.playlist.util.QueryUtils

class SongViewModel(application: Application): AndroidViewModel(application) {
    private var _playlistDBHelper: PlaylistDatabaseHelper = PlaylistDatabaseHelper(application)
    private var _playlist: MutableLiveData<ArrayList<Track>> = MutableLiveData()
    private var _artistslist: MutableLiveData<ArrayList<Artist>> = MutableLiveData()
    private val keyAPI = "6a3c1edcc48b911e444dd5ff05877d1f"

    fun getTopTracks(): MutableLiveData<ArrayList<Track>> {
        val query = "2.0/?method=chart.gettoptracks&api_key=$keyAPI&format=json"
       loadTracks(query, "0")
        return _playlist
    }

    fun searchTracksByArtist(artistName: String): MutableLiveData<ArrayList<Track>> {
        val query = "2.0/?method=artist.gettoptracks&artist=$artistName&api_key=$keyAPI&format=json"
        loadTracks(query, "1")
        return _playlist
    }

    fun getSimilar(trackName: String, artistName: String): MutableLiveData<ArrayList<Track>> {
        val query = "2.0/?method=track.getsimilar&artist=$artistName&track=$trackName&api_key=$keyAPI&format=json"
        loadTracks(query, "3")
        return _playlist
    }

    fun getTopArtists(): MutableLiveData<ArrayList<Artist>> {
        val query = "2.0/?method=chart.gettopartists&api_key=$keyAPI&format=json"
        TracksAsyncTask0().execute(query, "4")
        return _artistslist
    }

    fun getTrack(trackName: String, artistName: String): MutableLiveData<ArrayList<Track>> {
        val query = "2.0/?method=track.getInfo&api_key=$keyAPI&artist=$artistName&track=$trackName&format=json"
        loadTracks(query, "2")
        return _playlist
    }

    private fun loadTracks(query: String, code: String) {
        TracksAsyncTask().execute(query, code)
    }

    @SuppressLint("StaticFieldLeak")
    inner class TracksAsyncTask0: AsyncTask<String, Unit, ArrayList<Artist>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Artist>? {
            return QueryUtils.fetchTracksData0(params[0]!!, params[1]!!)
        }

        override fun onPostExecute(result: ArrayList<Artist>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            }
            else {
                Log.e("RESULTS", result.toString())

                _artistslist.value = result
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class TracksAsyncTask: AsyncTask<String, Unit, ArrayList<Track>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Track>? {
            return QueryUtils.fetchTracksData(params[0]!!, params[1]!!)
        }

        override fun onPostExecute(result: ArrayList<Track>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            }
            else {
                Log.e("RESULTS", result.toString())
                // Tag Items
                val saves = this@SongViewModel.loadSaved()
                val resultList = ArrayList<Track>()
                for (item in result) {
                    for (s in saves) {
                        if (s.getUrl() == item.getUrl()) {
                            item.isSaved = true
                        }
                    }
                    resultList.add(item)
                }
                _playlist.value = result
            }
        }
    }

    fun getSaved(): MutableLiveData<ArrayList<Track>> {
        val returnList = this.loadSaved()
        this._playlist.value = returnList
        return this._playlist
    }

    private fun loadSaved(): ArrayList<Track> {
        val saved: ArrayList<Track> = ArrayList()
        val database = this._playlistDBHelper.readableDatabase

        val cursor = database.query(
            DbSettings.DBPlaylistEntry.TABLE,
            null,
            null, null, null, null, DbSettings.DBPlaylistEntry.TRACK
        )

        while (cursor.moveToNext()) {
            val cursorTrack = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.TRACK)
            val cursorArtist = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.ARTIST)
            val cursorImage = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.IMAGE)
            val cursorPlaycount = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.PLAYCOUNT)
            val cursorListeners = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.LISTENERS)
            val cursorUrl = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.URL)

            val track = Track(
                cursor.getString(cursorTrack),
                cursor.getString(cursorArtist),
                cursor.getString(cursorImage),
                cursor.getInt(cursorPlaycount),
                cursor.getInt(cursorListeners),
                cursor.getString(cursorUrl)
            )
            track.isSaved = true
            saved.add(track)
        }
        cursor.close()
        database.close()

        return saved
    }

    fun addSaved(track: Track) {
        val database: SQLiteDatabase = this._playlistDBHelper.writableDatabase

        val savedValues = ContentValues()
        savedValues.put(DbSettings.DBPlaylistEntry.TRACK, track.getTrack())
        savedValues.put(DbSettings.DBPlaylistEntry.ARTIST, track.getArtist())
        savedValues.put(DbSettings.DBPlaylistEntry.IMAGE, track.getImage())
        savedValues.put(DbSettings.DBPlaylistEntry.PLAYCOUNT, track.getPlayCounts())
        savedValues.put(DbSettings.DBPlaylistEntry.LISTENERS, track.getListeners())
        savedValues.put(DbSettings.DBPlaylistEntry.URL, track.getUrl())
        database.insertWithOnConflict(
            DbSettings.DBPlaylistEntry.TABLE,
            null,
            savedValues,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        database.close()
    }

    fun removeSaved(url: String, isFromResultList: Boolean = false) {
        val database = _playlistDBHelper.writableDatabase

        database.delete(
            DbSettings.DBPlaylistEntry.TABLE,
            "${DbSettings.DBPlaylistEntry.URL}=?",
            arrayOf(url)
        )
        database.close()

        var index = 0
        val saves = this._playlist.value
        if(saves != null) {
            for (i in 0 until saves.size) {
                if(saves[i].getUrl() == url) {
                    index = i
                }
            }
            if(isFromResultList) {
                saves[index].isSaved = false
            }
            else {
                saves.removeAt(index)
            }
            this._playlist.value = saves
        }
    }
}