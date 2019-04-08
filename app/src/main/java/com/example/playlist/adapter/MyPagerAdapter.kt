package com.example.playlist.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.playlist.MainActivity
import com.example.playlist.R
import com.example.playlist.fragment.HomeFragment
import com.example.playlist.fragment.PlaylistFragment
import com.example.playlist.fragment.Attribute
import com.example.playlist.fragment.TopArtistsFragment


class MyPagerAdapter(fm: FragmentManager, myContext: Context): FragmentPagerAdapter(fm) {
    private val parentContext = myContext

    override fun getItem(p0: Int): Fragment {
        return when (p0) {
            0 -> {
                HomeFragment(parentContext)
            }
            1 -> {
                TopArtistsFragment(parentContext)
            }
            2 -> {
                PlaylistFragment(parentContext)
            }
            else -> {
                Attribute()
            }
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "TopTracks"
            1 -> "TopArtists"
            2 -> "Playlist"
            else -> "Attribute"
        }
    }
}