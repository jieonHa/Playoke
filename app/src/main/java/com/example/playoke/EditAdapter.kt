package com.example.playoke

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.playoke.databinding.ItemEditsongBinding
import com.example.playoke.databinding.ItemSongBinding

class EditAdapter(private val edits: List<Edit>) : RecyclerView.Adapter<EditAdapter.EditViewHolder>() {

    inner class EditViewHolder(private val binding: ItemEditsongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(edit: Edit) {
            binding.imageViewCover.setImageResource(edit.coverImageResId)
            binding.textViewTitle.text = edit.title
            binding.textViewArtist.text = edit.artist
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
        holder.bind(edits[position])
    }

    override fun getItemCount(): Int = edits.size
}
