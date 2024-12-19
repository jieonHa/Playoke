package com.example.playoke

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playoke.databinding.ActivityLyricsBinding
import com.example.playoke.databinding.ActivitySignInBinding
import com.example.playoke.databinding.ActivitySignUpBinding

class SignIn : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.toSignUp.setOnClickListener{ // 회원가입으로 이동
            val intent: Intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        binding.toIdPwFinding.setOnClickListener{ // 비밀번호 찾기로 이동
            val intent: Intent = Intent(this, pwReset::class.java)
            startActivity(intent)
        }

        binding.signInBtn.setOnClickListener {
            //이메일, 비밀번호 로그인.......................
            val email: String = binding.email.text.toString()
            val password: String = binding.pw.text.toString()
            MyApplication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.email.text.clear()
                    binding.pw.text.clear()
                    if (task.isSuccessful) {
                        if (MyApplication.checkAuth()) {
                            // 로그인 성공
                            MyApplication.email = email
                            val intent: Intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
// 발송된 메일로 인증 확인을 안 한 경우
                            Toast.makeText(baseContext,
                                "전송된 메일로 이메일 인증이 되지 않았습니다.",
                                Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(baseContext, "로그인 실패",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }

        binding.goBackBtn.setOnClickListener{ // 회원가입으로 이동
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}