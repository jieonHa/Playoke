package com.example.playoke

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playoke.databinding.ActivityPwResetBinding
import com.example.playoke.databinding.ActivitySignInBinding

class pwReset : AppCompatActivity() {
    lateinit var binding: ActivityPwResetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPwResetBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.pwResetBtn // 비밀번호 재설정 기능 추가

        binding.backToSignIn.setOnClickListener{
            val intent: Intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}