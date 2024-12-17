package com.example.playoke

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playoke.databinding.ActivityCreatePlaylistBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CreatePlaylistActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = Firebase.firestore
        val binding = ActivityCreatePlaylistBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.cancelBtn.setOnClickListener{
            finish()
        }
        binding.confirmBtn.setOnClickListener{
            val playlistName = binding.playlistName.text?.toString()
            if (!playlistName.isNullOrEmpty()) {
                val playlistRef = db.collection("users")
                    .document(UserInfo.key)
                    .collection("playlists")
                    .document(playlistName)

                playlistRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Document with the given name already exists
                            Toast.makeText(this, "Playlist already exists!", Toast.LENGTH_LONG).show()
                            Log.d("checking", "Playlist already exists!")
                        } else {
                            // Document does not exist, proceed to create it
                            playlistRef.set(mapOf("playlistImg" to ""))
                                .addOnSuccessListener { finish() }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Failed To Add Playlist ${e}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Error Checking Playlist: ${e}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }
}