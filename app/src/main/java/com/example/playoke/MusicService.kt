package com.example.playoke

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.os.Binder

class MusicService : Service() {
    private val binder = LocalBinder()
    lateinit var player: MediaPlayer
    private var currentPosition: Int = 0

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(this, R.raw.sunday_morning)
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