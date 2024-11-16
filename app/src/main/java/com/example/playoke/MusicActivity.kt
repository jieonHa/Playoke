package com.example.playoke

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playoke.databinding.ActivityMusicBinding

class MusicActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var play = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.sunday_morning)

        mediaPlayer?.setOnPreparedListener {
            // Get duration in milliseconds
            val duration = mediaPlayer?.duration ?: 0

            // Convert duration to seconds (optional)
            val durationInSeconds = duration / 1000
            val minutes = durationInSeconds/60
            val seconds = durationInSeconds - minutes * 60
            val musicDuration = "${minutes}.${seconds}"

            binding.musicDuration.setText(musicDuration)
        }

        binding.playPauseBtn.setOnClickListener{
            play = !play
            if (play){
                binding.playPauseBtn.setImageResource(R.drawable.ic_pause_circle_outline)
                mediaPlayer?.start()
            } else{
                binding.playPauseBtn.setImageResource(R.drawable.ic_play_circle_outline)
                mediaPlayer?.pause()
            }
        }
        binding.lyrics.setOnClickListener{
            val intent: Intent = Intent(this, LyricsActivity::class.java)
            startActivity(intent)
        }
    }
}