package com.example.playoke

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.view.View

class HomeAdapter(
    private val playlists: List<HomePlaylist>,
    private val onItemClick: (String) -> Unit // 클릭 이벤트 콜백
) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.titleTextView.text = playlist.title

        // Glide를 사용해 URL 이미지 로드
        Glide.with(holder.itemView.context)
            .load(playlist.coverImageUrl) // Firestore에서 가져온 URL
            .placeholder(R.drawable.img_music) // 로딩 중 기본 이미지
            .error(R.drawable.img_error) // 에러 발생 시 기본 이미지
            .into(holder.coverImageView)

        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            onItemClick(playlist.title) // 클릭 시 플레이리스트 ID 전달
        }
    }

    override fun getItemCount(): Int = playlists.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.tvPlaylistName)
        val coverImageView: ImageView = view.findViewById(R.id.ivItemcover)
    }
}
