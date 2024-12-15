package com.example.playoke

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playoke.databinding.ItemEditsongBinding
import com.example.playoke.databinding.ItemSongBinding

class EditAdapter(private val edits: List<Edit>) : RecyclerView.Adapter<EditAdapter.EditViewHolder>() {

    class EditViewHolder(private val binding: ItemEditsongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(edit: Edit) {
            binding.textViewTitle.text = edit.name
            binding.textViewArtist.text = edit.artist
            Glide.with(binding.root.context)
                .load(edit.coverImageUrl)  // Glide로 이미지 로딩
                .placeholder(R.drawable.img_music) // 로딩 중 기본 이미지
                .error(R.drawable.img_error) // 에러 발생 시 기본 이미지
                .into(binding.imageViewCover)

            binding.moreButton.setOnClickListener {
                // Handle 'more' button click
                Toast.makeText(it.context, "test", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditViewHolder {
        val binding = ItemEditsongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EditViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EditViewHolder, position: Int) {
        val edit = edits[position]
        holder.bind(edit)  // 데이터 바인딩
    }

    override fun getItemCount(): Int = edits.size
}
