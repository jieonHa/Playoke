package com.example.playoke

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.os.Binder
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.IOException

class MusicService : Service() {
    val db = Firebase.firestore
    var musicName :String = "Unknown Name"
    var artistName:String = "Unknown Artist"
    var imageSrc: String = ""
    private val binder = LocalBinder()
    lateinit var player: MediaPlayer
    private var currentPosition: Int = 0

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer()
        db.collection("musics").document("music-1").get()
            .addOnSuccessListener{ document->
                Log.d("test", "yyy")
                if (document.exists()){

                    musicName = document.getString("name")?:"Unknown Name"
                    artistName = document.getString("artist")?:"Unknown Artist"
                    imageSrc = document.getString("img")?:""
                    try {
                        player.reset()
                        player.setDataSource(document.getString("url"))
                        player.prepare()
                        Log.d("HappySuccess","yes!")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else{
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener{e->Log.d("error", "error ${e}")}
        //player = MediaPlayer.create(this, R.raw.sunday_morning)

    }

    fun isPlaying():Boolean{
        return player.isPlaying()
    }

    fun startMusic() {
        player?.start()
    }

    fun pauseMusic() {
        player?.pause()
    }

    fun getCurrentPosition(): Int {
        return player.currentPosition
    }

    fun getDuration(): Int {
        return player.duration
    }

    fun getDurationText():String{
        val duration = player.duration
        val durationInSeconds = duration / 1000
        val minutes = durationInSeconds/60
        val seconds = durationInSeconds - minutes * 60
        return "${minutes}:${seconds}"
    }

    fun seekTo(position: Int) {
        player.seekTo(position)
    }

    fun changeMusicName(title: TextView){
        title.text = musicName
    }

    fun getPlaybackPosition(): Int {
        return player?.currentPosition ?: 0
    }

    override fun onDestroy() {
        super.onDestroy()
        currentPosition = player.currentPosition // Save the position
        player.pause() // Pause instead of stopping
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

}