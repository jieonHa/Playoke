package com.example.playoke

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.playoke.databinding.ActivityMusicBinding

class MusicActivity : AppCompatActivity() {
    private var musicService: MusicService? = null
    private var isBound = false
    private var handler: Handler? = null
    private lateinit var binding: ActivityMusicBinding
    lateinit var musicName :String
    lateinit var artistName:String

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isBound = true
            updateMusicDuration()
            startSeekBarUpdate()
            if (!(musicService?.isPlaying()?:false)){
                binding.playPauseBtn.setImageResource(R.drawable.ic_play_circle_outline)
            } else{
                binding.playPauseBtn.setImageResource(R.drawable.ic_pause_circle_outline)
            }
            binding.musicName.setText(musicService!!.musicName)
            binding.artistName.setText(musicService!!.artistName)
        }
        override fun onServiceDisconnected(name: ComponentName) {
            musicService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        binding.idBtnDismiss.setOnClickListener{
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.addToPlaylist.setOnClickListener{
            val intent:Intent = Intent(this,AddToPlaylistActivity::class.java)
            startActivity(intent)
        }
        binding.lyrics.setOnClickListener{
            val intent: Intent = Intent(this, LyricsActivity::class.java)
            startActivity(intent)
        }

        binding.playPauseBtn.setOnClickListener{
            if (!(musicService?.isPlaying()?:false)){
                musicService?.startMusic()
                binding.playPauseBtn.setImageResource(R.drawable.ic_pause_circle_outline)
                startSeekBarUpdate()
            } else{
                musicService?.pauseMusic()
                binding.playPauseBtn.setImageResource(R.drawable.ic_play_circle_outline)
                stopSeekBarUpdate()
            }
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                stopSeekBarUpdate() // Pause updates while user interacts
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                startSeekBarUpdate() // Resume updates after interaction
            }
        })
    }
    private fun updateMusicDuration(){
        val duration = musicService?.getDurationText()?:"0:00"
        binding.musicDuration.setText(duration)
        binding.seekBar.max = musicService?.getDuration()?:0
    }
    private fun startSeekBarUpdate() {
        handler?.post(object : Runnable {
            override fun run() {
                musicService?.let {
                    val currentPosition = it.getCurrentPosition()
                    binding.seekBar.progress = currentPosition

                    // Update elapsed time
                    val minutes = currentPosition / 60000
                    val seconds = (currentPosition % 60000) / 1000
                    binding.elapsedTime.setText(String.format("%d:%02d", minutes, seconds))
                }
                handler?.postDelayed(this, 1000) // Update every second
            }
        })
    }

    private fun stopSeekBarUpdate() {
        handler?.removeCallbacksAndMessages(null)
    }
}