package com.example.playoke

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.playoke.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

private const val TAG_HOME = "home_fragment"
private const val TAG_SEARCH = "search_fragment"
private const val TAG_LIBRARY = "library_fragment"

class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private var musicService: MusicService? = null
    private var isBound = false
    private var handler: Handler? = null
    private lateinit var drawerLayout: DrawerLayout

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
            musicService?.player?.setOnCompletionListener(null)
            musicService?.setMediaPlayer(this@MainActivity, binding.songTitle, binding.songArtist, binding.songImage, binding.seekBar, false)
            Log.d("Testing", "Service Started")
            musicService?.player?.setOnCompletionListener {
                if (musicService?.fetchCurrentPosition() != 0) { // Ensure it was playing before triggering
                    Log.d("MusicService", "Track completed. Moving to the next track.")
                    musicService?.nextMusic(
                        this@MainActivity,
                        binding.songTitle,
                        binding.songArtist,
                        binding.songImage,
                        binding.seekBar
                    )
                }
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

        handler = Handler(Looper.getMainLooper())

        // 프래그먼트 전환
        setFragment(TAG_HOME, HomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> setFragment(TAG_HOME, HomeFragment())
                R.id.nav_search -> setFragment(TAG_SEARCH, SearchFragment())
                R.id.nav_library -> setFragment(TAG_LIBRARY, LibraryFragment())
            }
            true
        }

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

        binding.musicInfoContainer.setOnClickListener{
            val intent: Intent = Intent(this, MusicActivity::class.java)
            Log.d("testing", "working")
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
    }

    private fun updateMusicDuration(){
        binding.seekBar.max = musicService?.getDuration()?:0
    }
    private fun startSeekBarUpdate() {
        handler?.post(object : Runnable {
            override fun run() {
                musicService?.let {
                    val currentPosition = it.fetchCurrentPosition()
                    binding.seekBar.progress = currentPosition
                }
                handler?.postDelayed(this, 1000) // Update every second
            }
        })
    }

    private fun stopSeekBarUpdate() {
        handler?.removeCallbacksAndMessages(null)
    }


    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.fragmentContainer, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val search = manager.findFragmentByTag(TAG_SEARCH)
        val library = manager.findFragmentByTag(TAG_LIBRARY)

        if (home != null){
            fragTransaction.hide(home)
        }
        if (search != null) {
            fragTransaction.hide(search)
        }
        if (library != null){
            fragTransaction.hide(library)
        }

        if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }
        else if (tag == TAG_SEARCH) {
            if (search!=null){
                fragTransaction.show(search)
            }
        }
        else if (tag == TAG_LIBRARY){
            if (library != null){
                fragTransaction.show(library)
            }
        }
        fragTransaction.commitAllowingStateLoss()
    }
}