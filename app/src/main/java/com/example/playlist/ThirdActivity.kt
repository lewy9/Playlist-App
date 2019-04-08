package com.example.playlist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import com.example.playlist.model.Track
import com.example.playlist.viewmodel.SongViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_gridview.view.*

class ThirdActivity : AppCompatActivity() {
    private lateinit var viewModel: SongViewModel
    private lateinit var track: Track
    private var topTracks = ArrayList<Track>()
    private var adapter = TopTracksAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        track = intent.extras!!.getSerializable("TRACK") as Track
    }


    override fun onStart() {
        super.onStart()
        val rView: RecyclerView = rView
        rView.layoutManager = GridLayoutManager(this, 2, GridLayout.VERTICAL, false)
        viewModel = ViewModelProviders.of(this).get(SongViewModel::class.java)

        val observer = Observer<ArrayList<Track>> {
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
        viewModel.getSimilar(track.getTrack(), track.getArtist()).observe(this, observer)
    }
    override fun onBackPressed() {
        this.finish()
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
                Toast.makeText(this@ThirdActivity, "No similar tracks found", Toast.LENGTH_SHORT).show()
            }
            else {
                Picasso.with(this@ThirdActivity).load(trackImg).into(p0.trackImg)
            }
            p0.trackName.text = track.getTrack()
            p0.artist.text = track.getArtist()


            //  Navigate to 2nd activity
            p0.row.setOnClickListener {
                val intent = Intent(this@ThirdActivity, SecondActivity::class.java)
                intent.putExtra("TRACK", track)
                startActivity(intent)
            }
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
