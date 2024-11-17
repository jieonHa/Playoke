package com.example.playoke

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playoke.databinding.ActivityLyricsBinding
import com.example.playoke.databinding.ActivityMusicBinding

class LyricsActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var play = false
    private lateinit var binding: ActivityLyricsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLyricsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDismiss.setOnClickListener{
            val intent: Intent = Intent(this, MusicActivity::class.java)
            startActivity(intent)
        }
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
    }

}