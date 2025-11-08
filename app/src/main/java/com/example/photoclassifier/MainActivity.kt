package com.example.photoclassifier

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.photoclassifier.databinding.ActivityMainBinding

/**
 * 主Activity
 * 提供拍照、查看相册、管理文件夹的入口
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fileManager: FileManager

    // 权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            openCamera()
        } else {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileManager = FileManager(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnTakePhoto.setOnClickListener {
            checkPermissionsAndOpenCamera()
        }

        binding.btnViewGallery.setOnClickListener {
            openGallery()
        }

        binding.btnManageFolders.setOnClickListener {
            openGallery() // 在Gallery Activity中可以管理文件夹
        }
    }

    /**
     * 检查权限并打开相机
     */
    private fun checkPermissionsAndOpenCamera() {
        val requiredPermissions = mutableListOf(Manifest.permission.CAMERA)
        
        // Android 13+ 需要新的媒体权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10-12 不需要额外权限，使用 Scoped Storage
        } else {
            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            openCamera()
        } else {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    /**
     * 打开相机
     */
    private fun openCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    /**
     * 打开相册
     */
    private fun openGallery() {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }
}
