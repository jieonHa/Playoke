package com.example.playoke

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.example.playoke.databinding.ActivityMainBinding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var musicService: MusicService? = null
    private var isBound = false
    private lateinit var binding: ActivityMainBinding

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isBound = true
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

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        binding.playButton.setOnClickListener{
            if (!(musicService!!.isPlaying())){
                musicService?.startMusic()
                binding.playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline)
            } else{
                musicService?.pauseMusic()
                binding.playButton.setBackgroundResource(R.drawable.ic_play_circle_outline)
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

        setSupportActionBar(binding.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}