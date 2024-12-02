package com.example.playoke

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.os.Binder
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
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

    fun nextMusic(context:Context, musicName:TextView, artistName:TextView, musicImg: ImageView, seekbar: SeekBar){
        if(UserInfo.selectedPlaylist!=null){
            UserInfo.selectedMusic ++
            if(UserInfo.selectedMusic == UserInfo.playlistLength){
                UserInfo.selectedMusic = 0
            }
            Log.d("Firestore", "${UserInfo.selectedMusic}")
            //.collection("playlists").document(UserInfo.selectedPlaylist)
            db.collection("users").document(UserInfo.key).collection("playlists").document(UserInfo.selectedPlaylist).get()
                .addOnSuccessListener{ document->
                    if (document.exists()){
                        seekbar.progress = 0
                        UserInfo.playingMusic = document.getString("${UserInfo.selectedMusic}")?:"Unknown Name"
                        Log.d("Firestore", "${UserInfo.selectedMusic}, ${UserInfo.playingMusic}")
                        setMediaPlayer(context,musicName,artistName,musicImg, seekbar, true)
                    } else{
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener{e->Log.d("error", "error ${e}")}
        }
    }

    fun nextMusic(context:Context, musicName:TextView, artistName:TextView, musicImg: ImageView, musicDuration: TextView, seekbar: SeekBar){
        if(UserInfo.selectedPlaylist!=null){
            UserInfo.selectedMusic ++
            if(UserInfo.selectedMusic == UserInfo.playlistLength){
                UserInfo.selectedMusic = 0
            }
            Log.d("Firestore", "${UserInfo.selectedMusic}")
            //.collection("playlists").document(UserInfo.selectedPlaylist)
            db.collection("users").document(UserInfo.key).collection("playlists").document(UserInfo.selectedPlaylist).get()
                .addOnSuccessListener{ document->
                    if (document.exists()){
                        seekbar.progress = 0
                        UserInfo.playingMusic = document.getString("${UserInfo.selectedMusic}")?:"Unknown Name"
                        Log.d("Firestore", "${UserInfo.selectedMusic}, ${UserInfo.playingMusic}")
                        setMediaPlayer(context,musicName,artistName,musicImg,musicDuration, seekbar, true)
                    } else{
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener{e->Log.d("error", "error ${e}")}
        }
    }

    fun nextMusic(context:Context, musicName:TextView, artistName:TextView, musicImg: ImageView, musicDuration: TextView, seekbar: SeekBar, lyricsContainer:LinearLayout){
        if(UserInfo.selectedPlaylist!=null){
            UserInfo.selectedMusic ++
            if(UserInfo.selectedMusic == UserInfo.playlistLength){
                UserInfo.selectedMusic = 0
            }
            Log.d("Firestore", "${UserInfo.selectedMusic}")
            //.collection("playlists").document(UserInfo.selectedPlaylist)
            db.collection("users").document(UserInfo.key).collection("playlists").document(UserInfo.selectedPlaylist).get()
                .addOnSuccessListener{ document->
                    if (document.exists()){
                        seekbar.progress = 0
                        UserInfo.playingMusic = document.getString("${UserInfo.selectedMusic}")?:"Unknown Name"
                        Log.d("Firestore", "${UserInfo.selectedMusic}, ${UserInfo.playingMusic}")
                        setMediaPlayer(context,musicName,artistName,musicImg,musicDuration, seekbar, true, lyricsContainer)
                    } else{
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener{e->Log.d("error", "error ${e}")}
        }
    }

    fun previousMusic(context:Context, musicName:TextView, artistName:TextView, musicImg: ImageView, musicDuration: TextView, seekbar:SeekBar){
        if(UserInfo.selectedPlaylist!=null){
            UserInfo.selectedMusic --
            if(UserInfo.selectedMusic < 0){
                UserInfo.selectedMusic = UserInfo.playlistLength - 1
            }
            Log.d("Firestore", "${UserInfo.selectedMusic}")
            //.collection("playlists").document(UserInfo.selectedPlaylist)
            db.collection("users").document(UserInfo.key).collection("playlists").document(UserInfo.selectedPlaylist).get()
                .addOnSuccessListener{ document->
                    if (document.exists()){
                        UserInfo.playingMusic = document.getString("${UserInfo.selectedMusic}")?:"Unknown Name"
                        Log.d("Firestore", "${UserInfo.selectedMusic}, ${UserInfo.playingMusic}")
                        seekbar.progress = 0
                        setMediaPlayer(context,musicName,artistName,musicImg,musicDuration, seekbar, true)
                    } else{
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener{e->Log.d("error", "error ${e}")}
        }
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
        return String.format("%d:%02d", minutes, seconds)
    }

    fun seekTo(position: Int) {
        player.seekTo(position)
    }


    fun setMediaPlayer(context: Context, musicName:TextView, artistName:TextView, musicImg: ImageView, seekbar:SeekBar, startPlaying:Boolean){
        db.collection("musics").document(UserInfo.playingMusic).get()
            .addOnSuccessListener{ document->
                Log.d("test", "yyy")
                if (document.exists()){

                    musicName.setText(document.getString("name")?:"Unknown Name")
                    artistName.setText(document.getString("artist")?:"Unknown Artist")
                    Glide.with(context)
                        .load(document.getString("img"))
                        .into(musicImg)
                    //imageSrc = document.getString("img")?:""
                    try {
                        if (seekbar.progress==0){
                            player.reset()
                            player.setDataSource(document.getString("url"))
                            player.prepare()
                        }

                        seekbar.max = getDuration()?:0
                        if (startPlaying){
                            player.start()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else{
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener{e->Log.d("error", "error ${e}")}
    }

    fun setMediaPlayer(context: Context, musicName:TextView, artistName:TextView, musicImg: ImageView, musicDuration:TextView, seekbar:SeekBar, startPlaying:Boolean){
        db.collection("musics").document(UserInfo.playingMusic).get()
            .addOnSuccessListener{ document->
                Log.d("test", "yyy")
                if (document.exists()){

                    musicName.setText(document.getString("name")?:"Unknown Name")
                    artistName.setText(document.getString("artist")?:"Unknown Artist")
                    Glide.with(context)
                        .load(document.getString("img"))
                        .into(musicImg)
                    //imageSrc = document.getString("img")?:""
                    try {
                        if (seekbar.progress==0){
                            player.reset()
                            player.setDataSource(document.getString("url"))
                            player.prepare()
                        }
                        musicDuration.setText(getDurationText())
                        seekbar.max = getDuration()?:0
                        if (startPlaying){
                            player.start()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else{
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener{e->Log.d("error", "error ${e}")}
    }
    fun setMediaPlayer(context: Context, musicName:TextView, artistName:TextView, musicImg: ImageView, musicDuration:TextView, seekbar:SeekBar, startPlaying:Boolean, lyricsContainer:LinearLayout){
        db.collection("musics").document(UserInfo.playingMusic).get()
            .addOnSuccessListener{ document->
                Log.d("test", "yyy")
                if (document.exists()){
                    val lyrics = document.getString("lyrics") ?: "Unable To Load Lyrics"
                    musicName.setText(document.getString("name")?:"Unknown Name")
                    artistName.setText(document.getString("artist")?:"Unknown Artist")
                    Glide.with(context)
                        .load(document.getString("img"))
                        .into(musicImg)
                    lyricsContainer.removeAllViews()
                    lyrics.split("\\n").toTypedArray().forEach { line ->
                        val lineView = TextView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, // Width equivalent to "wrap_content"
                                LinearLayout.LayoutParams.WRAP_CONTENT  // Height equivalent to "wrap_content"
                            ).apply {
                                bottomMargin = 18.dpToPx(context) // Set marginBottom in dp
                            }
                            text = line // Set text
                            setTextColor(ContextCompat.getColor(context, R.color.dark_grey)) // Set text color
                            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.title)) // Set text size
                            gravity = Gravity.CENTER // Set gravity to center
                        }
                        lyricsContainer.addView(lineView)
                    }
                    //imageSrc = document.getString("img")?:""
                    try {
                        if (seekbar.progress==0){
                            player.reset()
                            player.setDataSource(document.getString("url"))
                            player.prepare()
                        }
                        musicDuration.setText(getDurationText())
                        seekbar.max = getDuration()?:0
                        if (startPlaying){
                            player.start()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else{
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener{e->Log.d("error", "error ${e}")}
    }


    fun setLyrics(context:Context, lyricsContainer: LinearLayout) {
        Log.d("checking", "working1")
        db.collection("musics").document(UserInfo.playingMusic).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val lyrics = document.getString("lyrics") ?: "Unable To Load Lyrics"
                    Log.d("checking", "${lyrics::class.simpleName}")
                    lyrics.split("\\n").toTypedArray().forEach { line ->
                        Log.d("checking", "line ${line}")
                        val lineView = TextView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, // Width equivalent to "wrap_content"
                                LinearLayout.LayoutParams.WRAP_CONTENT  // Height equivalent to "wrap_content"
                            ).apply {
                                bottomMargin = 18.dpToPx(context) // Set marginBottom in dp
                            }
                            text = line // Set text
                            setTextColor(ContextCompat.getColor(context, R.color.dark_grey)) // Set text color
                            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.title)) // Set text size
                            gravity = Gravity.CENTER // Set gravity to center
                        }
                        Log.d("checking", "working2: $line")
                        lyricsContainer.addView(lineView)
                    }
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.d("error", "Error: $e")
            }
    }

    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density + 0.5f).toInt()
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