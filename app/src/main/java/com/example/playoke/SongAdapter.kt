package com.example.playoke

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.playoke.databinding.ItemSongBinding

class SongAdapter(private val songs: List<Song>) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.imageViewCover.setImageResource(song.coverImageResId)
            binding.textViewTitle.text = song.title
            binding.textViewArtist.text = song.numberOfSongs.toString()
            binding.moreButton.setOnClickListener {
                // Handle 'more' button click
                Toast.makeText(it.context, "test", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size
}
