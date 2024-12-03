package com.example.playoke

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.view.View

class LibraryPlaylistAdapter(private val playlists: List<LibraryPlaylist>) :
    RecyclerView.Adapter<LibraryPlaylistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.titleTextView.text = playlist.title
        holder.songCountTextView.text = "음악 ${playlist.numberOfSongs}개"

        // Glide를 사용해 URL 이미지 로드
        Glide.with(holder.itemView.context)
            .load(playlist.coverImageUrl) // Firestore에서 가져온 URL
            .placeholder(R.drawable.img_music) // 로딩 중 기본 이미지
            .error(R.drawable.img_error) // 에러 발생 시 기본 이미지
            .into(holder.coverImageView)
    }

    override fun getItemCount(): Int = playlists.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.textViewTitle)
        val songCountTextView: TextView = view.findViewById(R.id.textViewSongCount)
        val coverImageView: ImageView = view.findViewById(R.id.imageViewCover)
    }
}

