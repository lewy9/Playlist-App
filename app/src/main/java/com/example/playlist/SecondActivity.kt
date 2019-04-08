package com.example.playlist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextClock
import android.widget.TextView
import com.example.playlist.model.Track
import com.example.playlist.viewmodel.SongViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.layout_gridview.view.*


class SecondActivity : AppCompatActivity() {
    private lateinit var viewModel: SongViewModel
    private lateinit var track: Track
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        track = intent.extras!!.getSerializable("TRACK") as Track
        Log.e("SAVED", track.isSaved.toString())

        viewModel = ViewModelProviders.of(this).get(SongViewModel::class.java)

        this.loadUI(track)

        btn.setOnClickListener{
            if (track.isSaved) {
                viewModel.removeSaved(track.getUrl())
                btn.text = "Add"
                Log.e("SAVED", track.isSaved.toString())
            }
            else {
                btn.text = "Delete"
                viewModel.addSaved(track)
                Log.e("SAVED", track.isSaved.toString())
            }
            track.isSaved = !track.isSaved
        }

        btn2.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("TRACK", track)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        this.finish()
    }

    private fun loadUI(myTrack: Track) {
        textView1.text = myTrack.getTrack()
        textView2.text = myTrack.getArtist()
        textView6.text = myTrack.getPlayCounts().toString()
        textView7.text = myTrack.getListeners().toString()
        if(myTrack.getImage().isEmpty()) {
            // Show nothing
        }
        else {
            Picasso.with(this).load(myTrack.getImage()).into(imageView)
        }
        if (myTrack.isSaved)
            btn.text = "Delete"
        else
            btn.text = "Add"
    }
}
