package com.example.playoke

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.playoke.databinding.ActivityMainBinding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var musicService: MusicService? = null
    private var isBound = false
    private var handler: Handler? = null
    private lateinit var binding: ActivityMainBinding

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isBound = true
            updateMusicDuration()
            startSeekBarUpdate()
            if (!(musicService?.isPlaying()?:false)){
                binding.playButton.setBackgroundResource(R.drawable.ic_play_circle_outline)
            } else{
                binding.playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline)
            }
        }
        override fun onServiceDisconnected(name: ComponentName) {
            musicService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        binding.playButton.setOnClickListener{
            if (!(musicService!!.isPlaying())){
                musicService?.startMusic()
                binding.playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline)
                startSeekBarUpdate()
            } else{
                musicService?.pauseMusic()
                binding.playButton.setBackgroundResource(R.drawable.ic_play_circle_outline)
                stopSeekBarUpdate()
            }
        }

        binding.musicBottomSheetTrigger.setOnClickListener{
            val intent: Intent = Intent(this, MusicActivity::class.java)
            startActivity(intent)
        }
        binding.plusButton.setOnClickListener{
            val intent: Intent = Intent(this, AddToPlaylistActivity::class.java)
            startActivity(intent)
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

        setSupportActionBar(binding.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun updateMusicDuration(){
        binding.seekBar.max = musicService?.getDuration()?:0
    }
    private fun startSeekBarUpdate() {
        handler?.post(object : Runnable {
            override fun run() {
                musicService?.let {
                    val currentPosition = it.getCurrentPosition()
                    binding.seekBar.progress = currentPosition
                }
                handler?.postDelayed(this, 1000) // Update every second
            }
        })
    }

    private fun stopSeekBarUpdate() {
        handler?.removeCallbacksAndMessages(null)
    }
}