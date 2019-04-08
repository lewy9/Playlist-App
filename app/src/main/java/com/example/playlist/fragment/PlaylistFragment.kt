package com.example.playlist.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.playlist.R
import com.example.playlist.SecondActivity
import com.example.playlist.model.Track
import com.example.playlist.viewmodel.SongViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_gridview.view.*
import kotlinx.android.synthetic.main.layout_listview.view.*

@SuppressLint("ValidFragment")
class PlaylistFragment(context: Context): Fragment() {
    private var parentContext: Context = context
    private var topTracks = ArrayList<Track>()
    private lateinit var viewModel: SongViewModel
    private var adapter = TopTracksAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onStart() {
        super.onStart()
        rView.layoutManager = LinearLayoutManager(parentContext)
        rView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        viewModel = ViewModelProviders.of(this).get(SongViewModel::class.java)

        val observer = Observer<ArrayList<Track>> {
            rView.adapter = adapter
            topTracks = it ?: ArrayList()
        }
        viewModel.getSaved().observe(this, observer)

    }

    inner class TopTracksAdapter: RecyclerView.Adapter<TopTracksAdapter.TopTracksViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TopTracksViewHolder {
            val trackView = LayoutInflater.from(p0.context).inflate(R.layout.layout_listview, p0, false)
            return TopTracksViewHolder(trackView)
        }

        override fun onBindViewHolder(p0: TopTracksViewHolder, p1: Int) {
            // p1 : position
            val track = topTracks[p1]

            p0.trackName.text = track.getTrack()
            p0.artist.text = track.getArtist()

            //  Navigate to 2nd activity
            p0.row.setOnClickListener {
                val intent = Intent(this@PlaylistFragment.parentContext, SecondActivity::class.java)
                intent.putExtra("TRACK", track)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return topTracks.size
        }

        inner class TopTracksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val row = itemView
            val trackName: TextView = itemView.textView1
            val artist: TextView = itemView.textView2
        }
    }
}