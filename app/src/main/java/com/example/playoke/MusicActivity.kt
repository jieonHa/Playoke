package com.example.playoke

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.playoke.databinding.ActivityMusicBinding

class MusicActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var play = false
    private var handler: Handler? = null
    private lateinit var binding: ActivityMusicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.sunday_morning)

        mediaPlayer?.setOnPreparedListener {
            // Get duration in milliseconds
            val duration = mediaPlayer?.duration ?: 0
            binding.seekBar.max = duration

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
                startSeekBarUpdate()
            } else{
                binding.playPauseBtn.setImageResource(R.drawable.ic_play_circle_outline)
                mediaPlayer?.pause()
                stopSeekBarUpdate()
            }
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress) // Seek to user's selected position
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Pause updating SeekBar while user is adjusting it
                stopSeekBarUpdate()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Resume updating SeekBar after user finishes adjusting
                startSeekBarUpdate()
            }
        })

        binding.lyrics.setOnClickListener{
            val intent: Intent = Intent(this, LyricsActivity::class.java)
            startActivity(intent)
        }

    }
    private fun startSeekBarUpdate() {
        handler = Handler(Looper.getMainLooper())
        handler?.post(object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    val currentPosition = it.currentPosition
                    binding.seekBar.progress = currentPosition

                    // Update elapsed time
                    val minutes = currentPosition / 60000
                    val seconds = (currentPosition % 60000) / 1000
                    binding.elapsedTime.text = String.format("%d.%02d", minutes, seconds)
                }
                handler?.postDelayed(this, 1000) // Update every second
            }
        })
    }

    private fun stopSeekBarUpdate() {
        handler?.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        stopSeekBarUpdate()
    }
}