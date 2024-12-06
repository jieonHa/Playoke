package com.example.playoke

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playoke.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {
    lateinit var binding:ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.signUpBtn.setOnClickListener{
            val email: String = binding.email.text.toString()
            val password: String = binding.passwd.text.toString()
            Log.d("testing", "Button is clicked")
            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task->
                    binding.email.text.clear()
                    binding.passwd.text.clear()
                    if (task.isSuccessful){
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { sendTask ->
                                if (sendTask.isSuccessful) {
                                    Log.d("testing","회원가입 성공")
                                    Toast.makeText(baseContext,
                                        "회원가입에 성공하였습니다. 전송된 메일을 확인해 주세요.",
                                        Toast.LENGTH_SHORT).show()
                                    val intent: Intent = Intent(this, SignIn::class.java)
                                    startActivity(intent)
                                } else {
                                    Log.d("testing","메일 전송 실패")
                                    Toast.makeText(baseContext, "메일 전송 실패",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Log.d("testing", "회원가입 실패")
                        Toast.makeText(baseContext, "회원가입 실패",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}