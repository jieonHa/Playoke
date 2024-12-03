package com.example.playoke

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playoke.databinding.ActivityAccessPermissionBinding

class AccessPermission : AppCompatActivity() {
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val permissionRequestCode = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access_permission) // 레이아웃 설정

        val binding = ActivityAccessPermissionBinding.inflate(layoutInflater)

        val requestButton: Button = findViewById(R.id.requset_perm_btn)
    //    val statusText: TextView = findViewById(R.id.tv_permission_status)

    /*    // 권한 상태를 텍스트뷰에 표시
        statusText.text = if (arePermissionsGranted()) {
            "All permissions are granted."
        } else {
            "Some permissions are missing. Please allow them."
        }
*/
        // 버튼 클릭 시 권한 요청
        requestButton.setOnClickListener {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, requiredPermissions, permissionRequestCode)
    }

    private fun arePermissionsGranted(): Boolean {
        return requiredPermissions.all { permission ->
            ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionRequestCode) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Permissions required to proceed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val status = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION")
        if (status == PackageManager.PERMISSION_GRANTED)
        setContentView(R.layout.activity_access_permission)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}   */