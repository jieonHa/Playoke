package com.example.playoke

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.playoke.databinding.ActivityAddToPlaylistBinding
import com.example.playoke.databinding.ViewholderPlaylistBinding

class PlaylistViewHolder(val binding:ViewholderPlaylistBinding): RecyclerView.ViewHolder(binding.root)
class PlaylistAdapter(val data:MutableList<String>):RecyclerView.Adapter<PlaylistViewHolder>(){
    override fun getItemCount():Int = data.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):PlaylistViewHolder =
        PlaylistViewHolder(ViewholderPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder:PlaylistViewHolder, position:Int){
        holder.binding.playlistName.text = data[position]
    }
}
class AddToPlaylistActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddToPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeButtonEnabled(true); // disable the button
        supportActionBar?.setDisplayHomeAsUpEnabled(true); // remove the left caret
        supportActionBar?.setDisplayShowHomeEnabled(true)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "플레이리스트에 추가하기"
        //val upArrow = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_add)
        //supportActionBar?.setHomeAsUpIndicator(upArrow)

        val data = mutableListOf<String>()
        for (i in 1..10){
            data.add("Playlist $i")
        }
        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        binding.recyclerView.adapter=PlaylistAdapter(data)

    }
}