package com.example.playoke

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playoke.databinding.ActivityMainPageBinding

class MainPageActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainPageBinding by lazy { ActivityMainPageBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.mainDrawerView.setNavigationItemSelectedListener {
            Log.d("son", "nav")
            true
        }
        }
    }
}