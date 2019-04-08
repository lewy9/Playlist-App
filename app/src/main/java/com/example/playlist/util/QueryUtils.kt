package com.example.playlist.util

import android.text.TextUtils
import android.util.Log
import com.example.playlist.model.Artist
import com.example.playlist.model.Track
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

class QueryUtils {
    companion object {
        private val LogTag = this::class.java.simpleName
        private const val BaseURL = "http://ws.audioscrobbler.com/"
        private const val APIkey = "6a3c1edcc48b911e444dd5ff05877d1f"

        fun fetchTracksData0(jsonQueryString: String, code: String): ArrayList<Artist>? {
            val url: URL? = createUrl("${this.BaseURL}$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            }
            catch (error: IOException) {
                Log.e(this.LogTag, "Problem occurs when making HTTP request.", error)
            }

            return extractDataFromJsonTopArtists(jsonResponse)

        }

        fun fetchTracksData(jsonQueryString: String, code: String): ArrayList<Track>? {
            val url: URL? = createUrl("${this.BaseURL}$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            }
            catch (error: IOException) {
                Log.e(this.LogTag, "Problem occurs when making HTTP request.", error)
            }

            if (code == "2")
                return extractDataFromJsonTrackInfo(jsonResponse)

            return extractDataFromJsonTopTracks(jsonResponse, code)
        }

        private fun createUrl(stringUrl: String): URL? {
            var url: URL? = null
            try {
                url = URL(stringUrl)
            }
            catch (error: MalformedURLException) {
                Log.e(this.LogTag, "Problem occurs when building URL.", error)
            }

            return url
        }

        private fun makeHttpRequest(url: URL?): String {
            var jsonResponse = ""

            if(url == null) {
                return jsonResponse
            }

            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.readTimeout = 10000
                urlConnection.connectTimeout = 15000
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                if(urlConnection.responseCode == 200) {
                    inputStream = urlConnection.inputStream
                    jsonResponse = readFromStream(inputStream)
                }
                else {
                    Log.e(this.LogTag, "error response code: ${urlConnection.responseCode}")
                }
            }
            catch (error: IOException) {
                Log.e(this.LogTag, "Problem retrieving the track data results: $url", error)
            }
            finally {
                urlConnection?.disconnect()
                inputStream?.close()
            }
            return jsonResponse
        }

        private fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
                val reader = BufferedReader(inputStreamReader)
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
            }

            return output.toString()
        }

        private fun extractDataFromJsonTopTracks(songJson: String?, code: String): ArrayList<Track>? {
            if(TextUtils.isEmpty(songJson)) {
                return null
            }

            var topTracks: ArrayList<Track> = ArrayList()
            try {
                if (code == "0") {
                    val baseJR = JSONObject(songJson).getJSONObject("tracks").getJSONArray("track")
                    topTracks = fetchTop(baseJR)
                }
                else if (code == "1") {
                    val baseJR = JSONObject(songJson).getJSONObject("toptracks").getJSONArray("track")
                    topTracks = fetchTop(baseJR)
                }
                else if (code == "3") {
                    val baseJR = JSONObject(songJson).getJSONObject("similartracks").getJSONArray("track")
                    topTracks = fetchTop(baseJR)
                }
            }
            catch (error: JSONException) {
                Log.e(this.LogTag, "Problem occurs when parsing JSON data.", error)
            }
            return topTracks
        }

        private fun extractDataFromJsonTopArtists(artistJson: String?): ArrayList<Artist>? {
            if(TextUtils.isEmpty(artistJson)) {
                return null
            }

            val artists: ArrayList<Artist> = ArrayList()
            try {
                val baseJsonResponse = JSONObject(artistJson).getJSONObject("artists").getJSONArray("artist")
                for (i in 0 until baseJsonResponse.length()) {
                    val artistObject = baseJsonResponse.getJSONObject(i)
                    val image = returnValueOrDefault<JSONArray>(artistObject, "image") as JSONArray?
                    val imageURL = ArrayList<String>()
                    if (image != null) {
                        for (j in 0 until image.length()) {
                            val imageItem = image.getJSONObject(j)
                            val url = returnValueOrDefault<String>(imageItem, "#text") as String
                            imageURL.add(url)
                        }
                    }

                    val newArtist = Artist(
                        returnValueOrDefault<String>(artistObject, "name") as String,
                        imageURL[3],
                        "",
                        returnValueOrDefault<String>(artistObject, "url") as String
                    )
                    artists.add(newArtist)
                }
            }
            catch (error: JSONException) {
                Log.e(this.LogTag, "Problem occurs when parsing JSON data.", error)
            }
            return artists
        }

        private fun extractDataFromJsonTrackInfo(songJson: String?): ArrayList<Track>? {
            if(TextUtils.isEmpty(songJson)) {
                return null
            }

            val tracks: ArrayList<Track> = ArrayList()
            try {
                val songObject = JSONObject(songJson).getJSONObject("track")
                val image = returnValueOrDefault<JSONArray>(songObject, "image") as JSONArray?
                val imageURL = ArrayList<String>()
                if(image != null) {
                    for (j in 0 until image.length()) {
                        val imageItem = image.getJSONObject(j)
                        val url = returnValueOrDefault<String>(imageItem, "#text") as String
                        imageURL.add(url)
                    }
                }

                val artistInfo = songObject.getJSONObject("artist")
                val track = Track(
                    returnValueOrDefault<String>(songObject, "name") as String,
                    returnValueOrDefault<String>(artistInfo, "name") as String,
                    imageURL[3],
                    returnValueOrDefault<Int>(songObject, "playcount") as Int,
                    returnValueOrDefault<Int>(songObject, "listeners") as Int,
                    returnValueOrDefault<String>(songObject, "url") as String
                )
                tracks.add(track)
            }
            catch (error: JSONException) {
                Log.e(this.LogTag, "Problem occurs when parsing JSON data.", error)
            }
            return tracks
        }

        private fun fetchTop(baseJsonResponse: JSONArray): ArrayList<Track> {
            val topTracks = ArrayList<Track>()
            for(i in 0 until baseJsonResponse.length()) {
                val songObject = baseJsonResponse.getJSONObject(i)
                val image = returnValueOrDefault<JSONArray>(songObject, "image") as JSONArray?
                val imageURL = ArrayList<String>()
                if(image != null) {
                    for(j in 0 until image.length()) {
                        val imageItem = image.getJSONObject(j)
                        val url = returnValueOrDefault<String>(imageItem, "#text") as String
                        imageURL.add(url)
                    }
                }

                val artistInfo = songObject.getJSONObject("artist")
                val newTrack = Track(
                    returnValueOrDefault<String>(songObject, "name") as String,
                    returnValueOrDefault<String>(artistInfo, "name") as String,
                    imageURL[3],
                    returnValueOrDefault<Int>(songObject, "playcount") as Int,
                    returnValueOrDefault<Int>(songObject, "listeners") as Int,
                    returnValueOrDefault<String>(songObject, "url") as String
                )
                topTracks.add(newTrack)
            }
            return topTracks
        }

        private inline fun <reified T> returnValueOrDefault(json: JSONObject, key: String): Any? {
            when (T::class) {
                String::class -> {
                    return if (json.has(key)) {
                        json.getString(key)
                    } else {
                        ""
                    }
                }
                Int::class -> {
                    return if (json.has(key)) {
                        json.getInt(key)
                    }
                    else {
                        return -1
                    }
                }
                Double::class -> {
                    return if (json.has(key)) {
                        json.getDouble(key)
                    }
                    else {
                        return -1.0
                    }
                }
                Long::class -> {
                    return if (json.has(key)) {
                        json.getLong(key)
                    }
                    else {
                        return (-1).toLong()
                    }
                }
                JSONObject::class -> {
                    return if (json.has(key)) {
                        json.getJSONObject(key)
                    }
                    else {
                        return null
                    }
                }
                JSONArray::class -> {
                    return if (json.has(key)) {
                        json.getJSONArray(key)
                    }
                    else {
                        return null
                    }
                }
                else -> {
                    return null
                }
            }
        }
    }
}