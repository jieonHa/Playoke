package com.example.playoke

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playoke.databinding.ItemSongBinding

class SongAdapter(private val songs: List<Song>) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.textViewTitle.text = song.name
            binding.textViewArtist.text = song.artist
            Glide.with(binding.root.context)
                .load(song.coverImageUrl)  // Glide로 이미지 로딩
                .placeholder(R.drawable.img_music) // 로딩 중 기본 이미지
                .error(R.drawable.img_error) // 에러 발생 시 기본 이미지
                .into(binding.imageViewCover)

            binding.moreButton.setOnClickListener {
                // Handle 'more' button click
                Toast.makeText(it.context, "test", Toast.LENGTH_SHORT).show()
            }

            // 아이템 클릭 이벤트 처리
            binding.root.setOnClickListener {
                // 클릭 시 노래 재생
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song)  // 데이터 바인딩
    }

    override fun getItemCount(): Int = songs.size
}
