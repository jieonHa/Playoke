package com.example.playoke

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

        binding.pwResetBtn.setOnClickListener { // 비밀번호 재설정 기능 추가
            val email:String = binding.emailForPw.text.toString().trim()
            if (email.isEmpty()){
                Toast.makeText(this, "이메일 주소를 입력해주세요", Toast.LENGTH_SHORT)
            }
            MyApplication.auth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    binding.statusMsg.text = "$email 으로 비밀번호 재설정 메일이 전송되었습니다."
                }else{
                    binding.statusMsg.text = "Error: ${task.exception?.message}"
                }
            }
        }
        binding.backToSignIn.setOnClickListener{
            val intent: Intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
        }
}