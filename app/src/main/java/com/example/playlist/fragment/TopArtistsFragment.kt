package com.example.playlist.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.playlist.R
import com.example.playlist.SecondActivity
import com.example.playlist.model.Artist
import com.example.playlist.model.Track
import com.example.playlist.viewmodel.SongViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_gridview.view.*

@SuppressLint("ValidFragment")
class TopArtistsFragment(context: Context): Fragment() {
    private var parentContext: Context = context
    private var topTracks = ArrayList<Artist>()
    private lateinit var viewModel: SongViewModel
    private var initialized: Boolean = false
    private var adapter = TopTracksAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_topartists, container, false)
    }

    override fun onStart() {
        super.onStart()

        val rView: RecyclerView = rView
        rView.layoutManager = GridLayoutManager(parentContext, 2, GridLayout.VERTICAL, false)
        viewModel = ViewModelProviders.of(this).get(SongViewModel::class.java)

        val observer = Observer<ArrayList<Artist>> {
            rView.adapter = adapter
            val result = DiffUtil.calculateDiff(object: DiffUtil.Callback() {

                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    return topTracks[p0].getUrl() == topTracks[p1].getUrl()
                }

                override fun getOldListSize(): Int {
                    return topTracks.size
                }

                override fun getNewListSize(): Int {
                    if(it == null)
                        return 0
                    return it.size
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return topTracks[p0] == topTracks[p1]
                }
            })
            result.dispatchUpdatesTo(adapter)
            topTracks = it ?: ArrayList()
        }
        viewModel.getTopArtists().observe(this, observer)

    }

    inner class TopTracksAdapter: RecyclerView.Adapter<TopTracksAdapter.TopTracksViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TopTracksViewHolder {
            val trackView = LayoutInflater.from(p0.context).inflate(R.layout.layout_gridview, p0, false)
            return TopTracksViewHolder(trackView)
        }

        override fun onBindViewHolder(p0: TopTracksViewHolder, p1: Int) {
            // p1 : position
            val track = topTracks[p1]
            val trackImg = track.getImage()
            if(trackImg.isEmpty()) {
                // Show nothing
            }
            else {
                Picasso.with(this@TopArtistsFragment.parentContext).load(trackImg).into(p0.trackImg)
            }
            p0.trackName.text = track.getArtist()

        }

        override fun getItemCount(): Int {
            return topTracks.size
        }

        inner class TopTracksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val row = itemView
            val trackImg: ImageView = itemView.img
            val trackName: TextView = itemView.text1
            val artist: TextView = itemView.text2
        }
    }
}