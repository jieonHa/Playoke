package com.example.playoke

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.playoke.databinding.ActivityAddToPlaylistBinding
import com.example.playoke.databinding.ViewholderPlaylistBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class PlaylistViewHolder(val binding:ViewholderPlaylistBinding): RecyclerView.ViewHolder(binding.root)
class PlaylistAdapter(val context: Context, val data:MutableList<String>, val imgs:MutableList<String>):RecyclerView.Adapter<PlaylistViewHolder>(){
    override fun getItemCount():Int = data.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):PlaylistViewHolder =
        PlaylistViewHolder(ViewholderPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder:PlaylistViewHolder, position:Int){
        val db = Firebase.firestore
        holder.binding.playlistName.text = data[position]
        Glide.with(context)
            .load(imgs[position])
            .into(holder.binding.playlistImage)
        holder.binding.playlistCheckbox.setOnClickListener{
            val docRef = db.collection("users").document(UserInfo.key).collection("playlists").document(data[position])
            var number = 0
            docRef.get().addOnSuccessListener { doc ->
                number = doc.getLong("numberOfSongs")?.toInt()?:0
                Log.d("number","${number}")
                if (holder.binding.playlistCheckbox.isChecked){
                    docRef.update(mapOf("${number + 1}" to "${UserInfo.playingMusic}"))
                    docRef.update("numberOfSongs", FieldValue.increment(1))
                } else{
                    docRef.update("${number}", FieldValue.delete())
                    docRef.update("numberOfSongs", FieldValue.increment(-1))
                }
            }
            /*if (holder.binding.playlistCheckbox.isChecked){
                docRef.update(mapOf("${number + 1}" to "${UserInfo.playingMusic}"))
                docRef.update("numberOfSongs", FieldValue.increment(1))
            } else{
                docRef.update("${number}", FieldValue.delete())
                docRef.update("numberOfSongs", FieldValue.increment(-1))
            }*/

        }

    }
}
class AddToPlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddToPlaylistBinding
    private val db = Firebase.firestore
    private val data = mutableListOf<String>()
    private val imgs = mutableListOf<String>()
    private val playlists = mutableListOf<String>()
    //val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddToPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar setup
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "플레이리스트에 추가하기"

        // RecyclerView setup
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = PlaylistAdapter(this, data, imgs)

        // New Playlist Button
        binding.newPlaylistBtn.setOnClickListener {
            val intent = Intent(this, CreatePlaylistActivity::class.java)
            startActivity(intent)
        }
        binding.confirmBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when the activity resumes
        refreshPlaylistData()
    }

    private fun refreshPlaylistData() {
        imgs.clear()
        data.clear() // Clear the old data to avoid duplication
        db.collection("users").document(UserInfo.key).collection("playlists").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    data.add(document.id)
                    imgs.add("${document.getString("playlistImg")}")
                }
                binding.recyclerView.adapter?.notifyDataSetChanged() // Notify adapter of changes
            }
            .addOnFailureListener { e ->
                // Handle errors (optional)
                e.printStackTrace()
            }
    }

}