package com.example.playoke

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.playoke.databinding.ItemPlaylistBinding
import com.example.playoke.databinding.ItemSongBinding

class LibraryPlaylistAdapter(private val libraryplaylists: List<LibraryPlaylist>) : RecyclerView.Adapter<LibraryPlaylistAdapter.LibraryPlaylistViewHolder>() {

    inner class LibraryPlaylistViewHolder(private val binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(libraryplaylist: LibraryPlaylist) {
            binding.imageViewCover.setImageResource(libraryplaylist.coverImageResId)
            binding.textViewTitle.text = libraryplaylist.title
            binding.textViewArtist.text = libraryplaylist.numberOfSongs.toString()
            binding.moreButton.setOnClickListener {
                // Handle 'more' button click
                Toast.makeText(it.context, "test", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryPlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryPlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LibraryPlaylistViewHolder, position: Int) {
        holder.bind(libraryplaylists[position])  // 수정: playlists -> libraryplaylists
    }

    override fun getItemCount(): Int = libraryplaylists.size  // 수정: playlists -> libraryplaylists
}
