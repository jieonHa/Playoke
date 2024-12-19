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

        binding.pwResetBtn.setOnClickListener {
            val email:String = binding.emailForPw.text.toString().trim()
            if (email.isEmpty()){
                Toast.makeText(this, "이메일 주소를 입력해주세요", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            // 비밀번호 재설정 이메일 전송
            MyApplication.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 이메일 발송 성공
                        binding.statusMsg.text = "$email 으로 비밀번호 재설정 메일이 전송되었습니다."
                    } else {
                        // 이메일 발송 실패 (예외 처리)
                        val error = task.exception?.message ?: "알 수 없는 오류가 발생했습니다."
                        if (error.contains("no user record", ignoreCase = true)) {
                            binding.statusMsg.text = "해당 이메일은 Firebase에 등록되어 있지 않습니다."
                        } else {
                            binding.statusMsg.text = "메일 전송 실패: $error"
                        }
                    }
                }
        }
            /*MyApplication.auth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    binding.statusMsg.text = "$email 으로 비밀번호 재설정 메일이 전송되었습니다."
                }else{
                    binding.statusMsg.text = "Error: ${task.exception?.message}"
                }
            }
        }*/
        binding.backToSignIn.setOnClickListener{
            val intent: Intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
        }
}